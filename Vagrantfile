# -*- mode: ruby -*-
# vi: set ft=ruby :

# TODO - docker support might be nice
Vagrant.configure(2) do |config|


  config.vm.box = "ubuntu/trusty64_oraclejava8"
  
  config.vm.synced_folder ".", "/vagrant"

  config.vm.provider "virtualbox" do |v|
    v.memory = 2048
  end

  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.scope = :box
    config.cache.enable :apt
  end

  config.vm.provision "ansible" do |ansible|
    ansible.playbook = "ansible/seed.yml"
    ansible.sudo = true
  end

  config.vm.define "frontend" do |frontend|
    frontend.vm.network "private_network", ip: "192.168.42.101"
  end
  
  config.vm.define "frontend2" do |frontend|
    frontend.vm.network "private_network", ip: "192.168.42.201"
  end

  config.vm.define "ticker" do |ticker|
    ticker.vm.network "private_network", ip: "192.168.42.102"
  end

  config.vm.define "ticker2" do |ticker|
    ticker.vm.network "private_network", ip: "192.168.42.202"
  end

  config.vm.define "trade_db" do |trade_db|
    trade_db.vm.network "private_network", ip: "192.168.42.102"
  end

  config.vm.define "trade_db2" do |trade_db|
    trade_db.vm.network "private_network", ip: "192.168.42.102"
  end

  config.vm.define "securities_db" do |securities_db|
    securities_db.vm.network "private_network", ip: "192.168.42.102"
  end

  config.vm.define "securities_db2" do |securities_db|
    securities_db.vm.network "private_network", ip: "192.168.42.102"
  end


  config.vm.define "network_trade" do |network_trade|
    network_trade.vm.network "private_network", ip: "192.168.42.102"
  end

  config.vm.define "network_trade2" do |network_trade|
    network_trade.vm.network "private_network", ip: "192.168.42.102"
  end

  #(2..4).each do |i|
  #   config.vm.define "member_#{i}" do |member|
  #      member.vm.network "private_network", ip: "192.168.11.2#{i}"
  #    end
  #end
end
