sudo /etc/init.d/red5 stop
sudo iptables -A INPUT -p tcp -m tcp --dport 5080 -j DROP
sudo iptables -A INPUT -p tcp -m tcp --dport 1935 -j DROP
sudo rm -rf /etc/init.d/red5
sudo rm -rf /usr/local/red5
sudo yum remove -y wget
sudo yum remove -y ant
sudo rm -rf bashrc
