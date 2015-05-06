# Mesos HDFS Framework REST API

* [Health](#health-api) 
  * [GET /health]
  * [GET /health/namenodes]
* [Config](#config-api)
* [Resource server](#resource-server-api)

## Health API

### GET /health
Aggregates health statuses of various components of HDFS framework, and returns them as a JSON response.

Request:
GET http://<scheduler-host>:<port>/health

Response:

```json
{
    "schedulerHealth": {
        "state": "Running"
    },
    "nameNodesHealth": {
        "nameNode1Initialized": false,
        "nameNode2Initialized": false
    }
}
```

### GET /health/namenodes
Returns the health status of namenodes as a JSON response.

Request:
GET http://<scheduler-host>:<port>/health/namenodes

Response:

```json
{
    "nameNodesHealth": {
        "nameNode1Initialized": false,
        "nameNode2Initialized": false
    }
}
```

## Config API

## Resource Server API

