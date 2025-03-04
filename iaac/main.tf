terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

resource "aws_apigatewayv2_api" "http_api" {
  name          = "order-http-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "stage" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "v1_0_0"
  auto_deploy = true
}

resource "aws_dynamodb_table" "table" {
  name           = "Order"
  billing_mode   = "PROVISIONED"
  read_capacity  = 25
  write_capacity = 25
  hash_key       = "partitionKey"
  range_key      = "sortKey"
  attribute {
    name = "partitionKey"
    type = "S"
  }
  attribute {
    name = "sortKey"
    type = "S"
  }
  attribute {
    name = "username"
    type = "S"
  }
  ttl {
    attribute_name = "expiresAt"
    enabled        = true
  }
  global_secondary_index {
    name            = "AccountEmployeeUsernameIndex"
    hash_key        = "username"
    range_key       = "sortKey"
    write_capacity  = 3
    read_capacity   = 3
    projection_type = "ALL"
  }
}

resource "aws_s3_bucket" "bucket" {
  bucket        = "com-alvarengacarlos-order-www"
  force_destroy = true
}

# TODO: Implements authorizer lambda
