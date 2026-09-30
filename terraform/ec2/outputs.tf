output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.web.id
}

output "public_ip" {
  description = "Public IP address of the instance"
  value       = aws_instance.web.public_ip
}

output "url" {
  description = "Address of the nginx page"
  value       = "http://${aws_instance.web.public_ip}"
}
