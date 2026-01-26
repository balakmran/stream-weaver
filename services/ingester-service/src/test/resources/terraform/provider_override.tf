provider "aws" {
  endpoints {
    kinesis  = "http://localstack:4566"
    dynamodb = "http://localstack:4566"
  }
}
