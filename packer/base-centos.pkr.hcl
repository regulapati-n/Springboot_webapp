packer {
  required_plugins {
    googlecompute = {
      version = "~> v1.0"
      source  = "github.com/hashicorp/googlecompute"
    }
  }
}


variable "image_name" {

  default     = "assignment-5final"
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
  default     = "dev-demo-415618"
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

  provisioner "shell" {
    script = "./shell/webapp.sh"
  }

  provisioner "file" {
    source      = "./services/webapp.service"
    destination = "/tmp/"
  }

  provisioner "shell" {
    inline = [
      "sudo groupadd -f csye6225",
      "sudo useradd -s /sbin/nologin -g csye6225 -d /opt/csye6225 -m csye6225",
      "sudo mv /tmp/webapp.service /etc/systemd/system/",
      "sudo mv /tmp/CSYE6225-0.0.1-SNAPSHOT.jar /opt/csye6225/CSYE6225-0.0.1-SNAPSHOT.jar",
      "sudo chown -R csye6225:csye6225 /opt/csye6225/CSYE6225-0.0.1-SNAPSHOT.jar",
      "sudo chmod 750 /opt/csye6225/CSYE6225-0.0.1-SNAPSHOT.jar",
      "sudo dnf install java-17-openjdk -y",
      "sudo touch /opt/csye6225/application.properties",
      "sudo chown csye6225:csye6225 /opt/csye6225/application.properties",
      "sudo chmod 750 /opt/csye6225/application.properties",
      "sudo yum install mysql-server -y",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable webapp.service"
    ]
  }
}
