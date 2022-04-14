package models

//@DynamoDBTable(tableName = "exampleModel)
data class ExampleModel(
    //@DynamoDBHashKey(attributeName="name)
    var name: String = ""
)