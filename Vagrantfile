# -*- mode: ruby -*-
# vi: set ft=ruby :
#
# *SETUP INSTRUCTIONS*
#
#

BOX_NAME = "akka-exchange"
#ENV['BOX_NAME'] || "default"
# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"
#Check if you have the good Vagrant version to use docker provider...
Vagrant.require_version ">= 1.6.0"

#print "ENV Vars"
#ENV.each {|key, value| puts "#{key} is #{value}"}
# My attempt at remembering my shell fu before I recalled Vagrant files are ruby.
#AKKA_EXCHANGE_VERSION = `grep -m 1 project build.sbt | awk -F = '{print $2}' | tr -d \" | tr -d " "`
#AKKA_EXCHANGE_VERSION = open('build.sbt') { |f|
#  version_stub = f.grep(/val projectVersion\s*=\s*"([\d\w\-\.]+?)"/)
#  version_stub[0].scan(/"([\d\w\-\.]+?)"/)[0][0]
#}
AKKA_EXCHANGE_BASE_ARTIFACT = "akka-exchange"

COMMAND = ARGV[0]
TARGET_CONTAINER = ARGV[1]

# Commands we will run SBT Staging on
STAGE_COMMANDS = ['up', 'provision', 'reload']
# Commands we'll clean up SBT on
CLEAN_COMMANDS = ['destroy']
# Commands for which we check against if a target was specified to pause after container startup
CONTAINER_PAUSE_COMMANDS = STAGE_COMMANDS 


#define_method("post_container_pause") do |container|
  #if HAS_TARGET_CONTAINER
    #puts "\e[1m\e[42;30m  ⚛ Sleeping 30 seconds to let container '#{container}' initialize... ⚛  \e[0m\n"
    #sleep(30)
  #end
#end

HAS_ARG = TARGET_CONTAINER.nil? || (!TARGET_CONTAINER.nil? && TARGET_CONTAINER.empty?)
HAS_TARGET_CONTAINER = !HAS_ARG && !TARGET_CONTAINER.start_with?("-")

# Some debug
puts "Running Vagrant Command '#{COMMAND}'\n"
if HAS_TARGET_CONTAINER
  puts "Target Container: #{TARGET_CONTAINER}\n"
else 
  puts "No target container.\n"
end


Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # I find it terribly annoying to read logs on UTC
  # If you want this to work, install 
  #     vagrant plugin install vagrant-timezone
  #
  if Vagrant.has_plugin?("vagrant-timezone")
    config.timezone.value = "America/Los_Angeles"
  end

  config.ssh.insert_key = false

  # Disable synced folders for the Docker container
  # (prevents an NFS error on "vagrant up")
  config.vm.synced_folder ".", "/vagrant", disabled: true
  

  config.vm.provider "docker" do |docker|
    ##docker.vagrant_machine = AKKA_EXCHANGE_BASE_ARTIFACT
    ## May not be needed, as we can provision with docker-machine
    docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
  end


  # todo - add optional second nodes of each?
  config.vm.define "frontend-node", primary: true do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'frontend' node ⚛  \e[0m\n"
      system("sbt frontend/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & Docker staging for 'frontend' node ☢  \e[0m\n"
      system("sbt frontend/docker:stage")
    end

    c.vm.hostname = "frontend"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./frontend/target/docker/stage"
      docker.name = "frontend"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.ports = ["8080:8080"]
      c.vm.synced_folder ".", "/vagrant", disabled: true
    end

  end

  config.vm.define "debug-node" do |c|
    c.vm.hostname = "debugger"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./src/main/resources/docker-debug"
      docker.name = "debugger"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      #docker.link("frontend:seed")
      docker.ports = ["2222:2242"]
      docker.volumes = ["/var/run/docker.sock:/var/run/docker.sock:rw"]
    end

  end

  config.vm.define "shared-journal-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'shared-journal' node ⚛  \e[0m\n"
      system("sbt journal/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'shared-journal' node ☢  \e[0m\n"
      system("sbt journal/docker:stage")
    end


    c.vm.hostname = "shared-journal"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./journal/target/docker/stage"
      docker.name = "shared-journal"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
      #docker.ports = ["2551:2551"]
    end

  end

  config.vm.define "trader-db-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'trader-db' node ⚛  \e[0m\n"
      system("sbt traderDB/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'trader-db' node ☢  \e[0m\n"
      system("sbt traderDB/docker:stage")
    end

    c.vm.hostname = "trader-db"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./trader-db/target/docker/stage"
      docker.name = "trader-db"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
    end

  end


  config.vm.define "trade-engine-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'trade-engine' node ⚛  \e[0m\n"
      system("sbt tradeEngine/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'trade-engine' node ☢  \e[0m\n"
      system("sbt tradeEngine/docker:stage")
    end

    c.vm.hostname = "trade-engine"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./trade-engine/target/docker/stage"
      docker.name = "trade-engine"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
    end

  end


  config.vm.define "ticker-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'ticker' node ⚛  \e[0m\n"
      system("sbt ticker/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'ticker' node ☢  \e[0m\n"
      system("sbt ticker/docker:stage")
    end

    c.vm.hostname = "ticker"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./ticker/target/docker/stage"
      docker.name = "ticker"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
    end

  end

  config.vm.define "network-trade-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'network-trade' node ⚛  \e[0m\n"
      system("sbt networkTrade/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'network-trade' node ☢  \e[0m\n"
      system("sbt networkTrade/docker:stage")
    end

    c.vm.hostname = "network-trade"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./network-trade/target/docker/stage"
      docker.name = "network-trade"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
    end

  end

  config.vm.define "securities-db-node" do |c|
    if STAGE_COMMANDS.include? COMMAND
      print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for 'securities-db' node ⚛  \e[0m\n"
      system("sbt securitiesDB/docker:stage")
    end
    
    if CLEAN_COMMANDS.include? COMMAND
      print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & docker staging for 'securities-db' node ☢  \e[0m\n"
      system("sbt securitiesDB/docker:stage")
    end

    c.vm.hostname = "securities-db"
    c.vm.provider "docker" do |docker|
      docker.build_dir = "./securities-db/target/docker/stage"
      docker.name = "securities-db"
      # Because our images boot up a app directly, don't want it trying to connect SSH etc
      docker.has_ssh = false
      docker.vagrant_vagrantfile = "Vagrantfile.host"
			docker.vagrant_machine = "akka-exchange"
      docker.link("frontend:seed")
    end

  end


end
