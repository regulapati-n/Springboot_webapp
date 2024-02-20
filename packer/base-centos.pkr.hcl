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
  image_name   = "centone"
  machine_type = "e2-medium"
  source_image = "centos-stream-8-v20240110"
  ssh_username = "packer"
  use_os_login = "false"
  zone         = var.zone
  network      = "test"
  subnetwork   = "test"
  project_id   = "nikhil-csye-6225"
}

build {
  sources = ["source.googlecompute.ex"]
  provisioner "file" {
    source      = "./target/CSYE6225-0.0.1-SNAPSHOT.jar"
    destination = "/tmp"
  }

  provisioner "shell" {
    script = "./shell/webapp.sh"
  }
}
