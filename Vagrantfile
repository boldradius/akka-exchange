# -*- mode: ruby -*-
# vi: set ft=ruby :
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
  
  ENV['VAGRANT_DEFAULT_PROVIDER'] = 'docker'

  # Script currently only does publishLocal change if you don't want dev type mode
  config.vm.provision "shell", inline: "sbt docker:publishLocal"

  config.vm.provider "docker" do |d|
    d.image = "java:jdk-8"
    d.name = AKKA_EXCHANGE_BASE_ARTIFACT
    d.vagrant_machine = "default"

    
  end

  config.ssh.insert_key = false


  # todo - add optional second nodes of each?
  config.vm.provision "docker" do |d|
    d.run "frontend", 
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT}-frontend", 
          args: "-p 8080:8080 -h frontend"

    d.run "trade-engine",
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT}-trade-engine",
          args: "-h trade-engine"

    d.run "ticker",
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT}-ticker",
          args: "-h ticker"

    d.run "trader-db",
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT}-trader-db",
          args: "-h trader-db"

    d.run "securities-db",
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT-securities-db",
          args: "-h securities-db"

    d.run "network-trade",
          image: "${AKKA_EXCHANGE_BASE_ARTIFACT}-network-trade",
          args: "-h network-trade"
  end

end
