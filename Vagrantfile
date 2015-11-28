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
#if HAS_TARGET_CONTAINER
#  puts "Target Container: #{TARGET_CONTAINER}\n"
#else
#  puts "No target container.\n"
#end


Vagrant.configure(VAGRANTFILE_API_VERSION) do |c|

  # I find it terribly annoying to read logs on UTC
  # If you want this to work, install
  #     vagrant plugin install vagrant-timezone
  #
  if Vagrant.has_plugin?("vagrant-timezone")
    c.timezone.value = "America/Los_Angeles"
  end

  # Use docker-compose instead of the usual
  # regular Docker config which is challenging at best.
  if !Vagrant.has_plugin?("vagrant-docker-compose")
    print "Installing missing Docker Compose plugin..."
    system("vagrant plugin install vagrant-docker-compose")
  end



  c.ssh.insert_key = false


  c.vm.define "akka-exchange" do |config|
    # Friendly name for host
    config.vm.hostname = "akka-exchange"


    # Skip checks for updated vagrant box since it'll be our own
    config.vm.box_check_update = false

    # Use Vagrant's default insecure key
    #config.ssh.insert_key = false


    config.vm.box = "phusion/ubuntu-14.04-amd64"
    # The following line terminates all ssh connections. Therefore
    # Vagrant will be forced to reconnect.
    # That's a workaround to have the docker command in the PATH
    # (This is necessary to provision properly; else Vagrant crashes after
    # initialising VirtualBox and has to be run a second time)
    config.vm.provision "shell", inline:
       "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"

    # Setup the docker-ssh tool on the docker host
    # I do a lot of direct debugging on the docker host (vagrant's box)
    # so this is helpful for the debugger node, at least.
    config.vm.provision "shell", inline:
      %q(
          curl --fail -L -O https://github.com/phusion/baseimage-docker/archive/master.tar.gz && \
            tar xzf master.tar.gz && \
            sudo ./baseimage-docker-master/install-tools.sh
      )

    config.vm.provision "file",
              source: "./src/main/resources/docker-debug/bash_aliases",
              destination: ".bash_aliases"

    config.vm.provision :docker
    config.vm.provision :docker_compose, yml: "/vagrant/docker-compose.yml", rebuild: true, run: "always"


    config.vm.provider "virtualbox" do |vbox|
      vbox.name = "akka-exchange"
      vbox.cpus = 2
      # Memory good, given how many nodes we start
      vbox.memory = 8192
    end

    # Exposes port 8080 on localhost to the VirtualBox docker host,
    # which forwards to the frontend node
    # frontend-01
    #config.vm.network :forwarded_port, guest: 8080, host: 8082
    # frontend-02
    #config.vm.network :forwarded_port, guest: 8080, host: 8082
  end

  if STAGE_COMMANDS.include? COMMAND
    print "\e[1m\e[42;30m  ⚛ Using sbt to stage Docker for Akka Exchange ⚛  \e[0m\n"
    system("sbt docker:stage")
  end

  if CLEAN_COMMANDS.include? COMMAND
    print "\e[1m\e[41;30m  ☢ Using sbt to clean up code & Docker staging for Akka Exchange ☢  \e[0m\n"
    system("sbt clean")
  end


end
