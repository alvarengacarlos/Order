output "http_api_id" {
  value = aws_apigatewayv2_api.http_api.id
}

output "bucket_name" {
  value = aws_s3_bucket.bucket.bucket
}

output "table_name" {
  value = aws_dynamodb_table.table.name
}
