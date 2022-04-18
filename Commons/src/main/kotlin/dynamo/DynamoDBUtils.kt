package dynamo

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.fasterxml.jackson.databind.ObjectMapper
import constants.REGION_SA_EAST_1

object DynamoDBUtils {
    var dynamoDB: AmazonDynamoDB = AmazonDynamoDBClientBuilder
        .standard()
        .withRegion(REGION_SA_EAST_1)
        .build()
    var mapper: DynamoDBMapper = DynamoDBMapper(dynamoDB)
    var objectMapper: ObjectMapper = ObjectMapper()
}