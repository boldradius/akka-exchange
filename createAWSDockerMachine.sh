#!/usr/bin/env bash



docker-machine create --driver amazonec2 --amazonec2-instance-type t2.large --amazonec2-access-key "${AWS_ACCESS_KEY_ID:?The Environment variable AWS_ACCESS_KEY_ID must be set.}" --amazonec2-secret-key "${AWS_SECRET_ACCESS_KEY:?The Environment variable AWS_SECRET_ACCESS_KEY must be set.}" --amazonec2-zone "${AWS_ZONE:?You must set AWS_ZONE environment variable with a letter indicating the aws-east1 sub-zone}" --amazonec2-vpc-id "${AWS_VPC_ID:?The Environment variable AWS_VPC_ID must be set.}" aws-akka-exchange
