#!/bin/bash

sudo dnf install java-17-openjdk -y
# sudo yum install curl zip unzip -y
# curl -s "https://get.sdkman.io" | bash
# source "/home/packer/.sdkman/bin/sdkman-init.sh"
# sdk install java 21-oracle
echo "*********** starting SQL setup! **************"

sudo dnf install mysql-server -y
sudo systemctl start mysqld.service
mysql -u root  -e "CREATE DATABASE db;"
mysql -u root  -e "CREATE USER 'test'@'localhost' IDENTIFIED BY 'intTest@123';"
mysql -u root  -e "GRANT ALL ON *.* TO 'test'@'localhost';"
mysql -u root  -e "FLUSH PRIVILEGES;"

echo "************** SQL Setup Complete!************"

# sudo yum install maven -y
sudo systemctl enable mysqld.service
sudo systemctl start mysqld.service
