packer {
  required_plugins {
    googlecompute = {
      version = "~> v1.0"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}

variable "image_name" {
  default     = "webapp-os"
  description = "Name of the custom image"
}

variable "machine_type" {
  default     = "e2-medium"
  description = "Type of machine to create VM"
}

variable "zone" {
  default     = "us-east4-b"
  description = "zone in which you want to create image in"
}

variable "project_name" {
  default     = "nikhil-csye-6225"
  description = "name of the project in gcloud"
}

variable "vpc_name" {
  default     = "default"
  description = "Name of the VPC"
}

variable "source_image" {
  default = "centos-stream-8-v20240110"
}


source "googlecompute" "ex" {
  image_name   = var.image_name
  machine_type = var.machine_type
  source_image = var.source_image
  ssh_username = "packer"
  use_os_login = "false"
  zone         = var.zone
  network      = var.vpc_name
  project_id   = var.project_name
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
