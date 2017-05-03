ctx logger info "---START INSTALL---"
sudo yum install -y ant
ctx logger info "INSTALLED ANT"
echo 'export ANT_HOME=/usr/local/ant' >> /tmp/bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java' >> /tmp/bashrc
echo 'export PATH=$PATH:/usr/local/ant/bin' >> /tmp/bashrc
echo 'export CLASSPATH=.:$JAVA_HOME/lib/classes.zip' >> /tmp/bashrc
source /tmp/bashrc
cd /usr/local/src
ctx logger info "GO TO SRC"
sudo yum install -y wget
ctx logger info "INSTALLED WGET"
sudo wget https://github.com/Red5/red5-server/releases/download/v1.0.8-RELEASE/red5-server-1.0.8-RELEASE.tar.gz
ctx logger info "DOWNLOADED RED5"
sudo tar -zxvf red5-server-1.0.8-RELEASE.tar.gz
ctx logger info "TAR RED5"
sudo mv red5-server /usr/local/red5
ctx logger info "MOVED TO /usr/local/red5"
sudo wget -O /etc/init.d/red5 http://www.sohailriaz.com/downloads/red5.txt
ctx logger info "DOWNLOADED red5.txt"
sudo chmod +x /etc/init.d/red5
ctx logger info "CHMOD RED5"
sudo iptables -A INPUT -p tcp -m tcp --dport 5080 -j ACCEPT
sudo iptables -A INPUT -p tcp -m tcp --dport 1935 -j ACCEPT
ctx logger info "DONE OPEN SERVER"
cd /usr/local/red5/
ctx logger info "WENT TO /user/local/red5"
sudo /etc/init.d/red5 start
