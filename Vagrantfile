# -*- mode: ruby -*-
# vi: set ft=ruby :
#
# *SETUP INSTRUCTIONS*
#
# 1. docker-machine create -d virtualbox --virtualbox-memory 4096 --virtualbox-cpu-count 2 akka-exchange
#   - Adjust cpu count and memory as needed, remembering we'll run several nodes
#   * This sets up an expected virtualbox container for the akka-exchange system
#   
#
BOX_NAME = ENV['BOX_NAME'] || "default"
# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"
#Check if you have the good Vagrant version to use docker provider...
Vagrant.require_version ">= 1.6.0"

# My attempt at remembering my shell fu before I recalled Vagrant files are ruby.
#AKKA_EXCHANGE_VERSION = `grep -m 1 project build.sbt | awk -F = '{print $2}' | tr -d \" | tr -d " "`
AKKA_EXCHANGE_VERSION = open('build.sbt') { |f|
  version_stub = f.grep(/val projectVersion\s*=\s*"([\d\w\-\.]+?)"/)
  version_stub[0].scan(/"([\d\w\-\.]+?)"/)[0][0]
}
AKKA_EXCHANGE_BASE_ARTIFACT = "akka-exchange"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.ssh.insert_key = false

  # Disable synced folders for the Docker container
  # (prevents an NFS error on "vagrant up")
  config.vm.synced_folder ".", "/vagrant", disabled: true
  

  config.vm.provider "docker" do |docker|
    ##docker.vagrant_machine = AKKA_EXCHANGE_BASE_ARTIFACT
    ## May not be needed, as we can provision with docker-machine
    docker.vagrant_vagrantfile = "Vagrantfile.host"
  end


  # todo - add optional second nodes of each?
  #config.vm.define "docker" do |docker|


  config.vm.define "frontend-node" do |c|
    c.vm.provider "docker" do |docker|
      #docker.vagrant_machine = AKKA_EXCHANGE_BASE_ARTIFACT
      docker.image = "#{AKKA_EXCHANGE_BASE_ARTIFACT}-frontend"
      docker.name = AKKA_EXCHANGE_BASE_ARTIFACT
      docker.has_ssh = true
      docker.vagrant_vagrantfile = "Vagrantfile.host"
    end
    #c.vm.provision "docker" do |docker|
      #docker.run "frontend", 
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-frontend", 
            #args: "-p 8080:8080 -h frontend"
    #end
    c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  end


  #config.vm.define "trade-engine-node" do |c|
    #c.vm.provision "docker" do |docker|
      #docker.run "trade-engine",
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-trade-engine",
            #args: "-h trade-engine"
    #end
    #c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  #end

  #config.vm.define "ticker-node" do |c|
    #c.vm.provision "docker" do |docker|
      #docker.run "ticker",
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-ticker",
            #args: "-h ticker"
    #end
    #c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  #end

  #config.vm.define "trader-db-node" do |c|
    #c.vm.provision "docker" do |docker|
      #docker.run "trader-db",
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-trader-db",
            #args: "-h trader-db"
    #end
    #c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  #end

  #config.vm.define "securities-db-node" do |c|
    #c.vm.provision "docker" do |docker|
      #docker.run "securities-db",
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-securities-db",
            #args: "-h securities-db"
    #end
    #c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  #end

  #config.vm.define "network-trade-node" do |c|
    #c.vm.provision "docker" do |docker|
      #docker.run "network-trade",
            #image: "#{AKKA_EXCHANGE_BASE_ARTIFACT}-network-trade",
            #args: "-h network-trade"
    #end
    #c.vm.provision "shell", inline: "ps aux | grep 'sshd:' | awk '{print $2}' | xargs kill"
  #end
  #end


end
