terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

data "aws_caller_identity" "current" {}

locals {
  aws_region = "us-east-1"
  account_id = data.aws_caller_identity.current.account_id
}

provider "aws" {
  region = local.aws_region
}

resource "aws_cloudwatch_log_group" "log_group" {
  name              = "account-service"
  retention_in_days = 30
}

resource "aws_iam_role" "role" {
  name = "account-service-lambda-role"
  assume_role_policy = jsonencode({
    Version : "2012-10-17"
    Statement : [
      {
        Action : "sts:AssumeRole"
        Effect : "Allow"
        Principal : {
          Service : "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "policy" {
  name = "account-service-lambda-policy"
  role = aws_iam_role.role.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Resource = [
          "arn:aws:logs:${local.aws_region}:${local.account_id}:log-group:${aws_cloudwatch_log_group.log_group.name}:log-stream:*",
          "arn:aws:logs:${local.aws_region}:${local.account_id}:log-group:${aws_cloudwatch_log_group.log_group.name}"
        ]
      },
      {
        Effect = "Allow"
        Action = [
          "dynamodb:PutItem",
          "dynamodb:DeleteItem",
          "dynamodb:GetItem",
          "dynamodb:Query",
          "dynamodb:UpdateItem"
        ]
        Resource = [
          "arn:aws:dynamodb:${local.aws_region}:${local.account_id}:table/Order/index/*",
          "arn:aws:dynamodb:${local.aws_region}:${local.account_id}:table/Order"
        ]
      },
    ]
  })
}

resource "aws_lambda_function" "function" {
  function_name = "account-service"
  role          = aws_iam_role.role.arn
  environment {
    variables = {
      "JWT_SECRET" = var.jwt_secret
    }
  }
  handler = "com.alvarengacarlos.order.www.Handler::handleRequest"
  logging_config {
    log_format = "Text"
    log_group  = aws_cloudwatch_log_group.log_group.id
  }
  runtime   = "java21"
  s3_bucket = "com-alvarengacarlos-order-www"
  s3_key    = "account-service-1.0.0.jar"
  timeout   = 28
}

resource "aws_lambda_permission" "permission" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "arn:aws:execute-api:${local.aws_region}:${local.account_id}:${var.http_api_id}/*/*/${aws_lambda_function.function.function_name}/{proxy+}"
}

resource "aws_apigatewayv2_integration" "integration" {
  api_id                 = var.http_api_id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.function.invoke_arn
  payload_format_version = "1.0"
  request_parameters = {
    "overwrite:path" = "$request.path"
  }
}

resource "aws_apigatewayv2_route" "route" {
  api_id    = var.http_api_id
  route_key = "ANY /account-service/{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.integration.id}"
}
