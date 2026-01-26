# 1. The Kinesis Data Stream
resource "aws_kinesis_stream" "weaver_stream" {
  name             = "stream-weaver-events"
  shard_count      = 1
  retention_period = 24

  stream_mode_details {
    stream_mode = "PROVISIONED"
  }
}

# 2. DynamoDB: Checkpoint Store
# Tracks which messages have been "woven" (processed)
resource "aws_dynamodb_table" "weaver_checkpoints" {
  name         = "StreamWeaverMetadata"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "metadataKey"

  attribute {
    name = "metadataKey"
    type = "S"
  }
}

# 3. DynamoDB: Lock Registry
# Ensures only one Virtual Thread handles a specific shard at a time
resource "aws_dynamodb_table" "weaver_locks" {
  name         = "StreamWeaverLocks"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "lockKey"

  attribute {
    name = "lockKey"
    type = "S"
  }
}