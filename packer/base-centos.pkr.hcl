packer {
  required_plugins {
    googlecompute = {
      version = "~> v1.0"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}

variable "zone" {
  default = "us-east4-b"
}


source "googlecompute" "ex" {
  image_name   = "finalcentos"
  machine_type = "e2-medium"
  source_image = "centos-stream-8-v20240110"
  ssh_username = "packer"
  use_os_login = "false"
  zone         = var.zone
  network      = "default"
  project_id   = "dev-csye6225-414920"
}

build {
  sources = ["source.googlecompute.ex"]


  provisioner "file" {
    source      = "./target/CSYE6225-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/"
  }

  provisioner "file" {
    source      = "./src/main/resources/application.properties"
    destination = "/tmp/"
  }

  provisioner "shell" {
    script = "./shell/webapp.sh"
  }

  provisioner "file" {
    source      = "./services/webapp.service"
    destination = "/tmp/"
  }
  
  provisioner "shell" {
    inline = [
      "sudo mv /tmp/webapp.service /etc/systemd/system/",
      "sudo systemctl start mysqld.service",
      "sudo groupadd csye6225",
      "sudo useradd -g csye6225 -s /usr/sbin/nologin csye6225",
      "sudo usermod -aG csye6225 csye6225",
      "sudo chown -R csye6225:csye6225 /tmp/CSYE6225-0.0.1-SNAPSHOT.jar",
      "sudo chmod 750 /tmp/CSYE6225-0.0.1-SNAPSHOT.jar",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable webapp.service"
    ]
  }
}
