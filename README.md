# Dizplai Tech Test

## Tech Stack & Requirements

This project uses Java v17.0.12, Node v18.20.4, Angular CLI v18.2.6, MySQL v8.0.39, and Docker v27.3.1.

To run this project, all you need is Docker.


## Running

Just run `docker compose up`. This will do the following:

- Build an image for the application from sources
- Download a MySQL image
- Start containers for each.

You may see a few errors at first, as the application tries to connect to MySQL while it's still initialising.

Navigate to `http://localhost:8080` in your web browser to access the application.


## Administrator API

There are a few API endpoint for administrators to manage polls.
In a live application these should be secured with HTTPS and RBAC, but as this is a demo project I've only implement a single in-memory `admin` user secured by basic authentication.
The username and password of the admin user are both `admin`.


### Create Poll

Saves the request body as a poll to the database with status `PENDING`.

URL: `POST /poll?activate=true|false`

| Query Parameter | Type    | Default | Description                |
|-----------------|---------|---------|----------------------------|
| activate        | boolean | false   | Activates created poll now |

Body: `CreatePoll` JSON Object

| Field    | Type           | Description                                |
|----------|----------------|--------------------------------------------|
| question | String         | The question that the poll asks            |
| options  | List of String | The options that the user may respond with |

Expected Responses:
* `200 OK`: returns created `poll` object
* `400 BAD REQUEST`: Poll question cannot be empty
* `400 BAD REQUEST`: Require at least 2 poll options
* `400 BAD REQUEST`: Cannot have more than 7 poll options


### Get Sample Poll

Returns a sample request body that can be passed to `POST /poll` (above).

URL: `GET /poll/sample`

Expected Responses:
* `200 OK`: returns sample `CreatePoll` JSON object


### Create Sample Poll

Rather than getting the sample poll then posting it, this endpoint saves the sample poll directly to the database and returns the created object.

URL: `POST /poll/sample`

| Query Parameter | Type    | Default | Description                |
|-----------------|---------|---------|----------------------------|
| activate        | boolean | false   | Activates created poll now |


Expected Reponses:
* `200 OK`: returns created sample poll


### Get Poll By ID

Returns the poll with the given ID.

URL: `GET /poll/{pollId}`

Expected Responses:
* `200 OK`: returns found poll
* `400 BAD REQUEST`: Poll not found


### Activate Poll by ID

Sets the status of the given poll to `ACTIVE` and the current active poll to `CLOSED`.

URL: `PUT /{pollId}/activate`

Expected Responses:
* `200 OK`: returns activated poll
* `400 BAD REQUEST`: Poll not found


### Activate Next Poll

Finds a poll with status `PENDING` and sets it to `ACTIVE` while closing the current active poll.

URL: `POST /activate/next`

Expected Responses:
* `200 OK`: returns activated poll
* `404 NOT FOUND`: No pending polls found


### View Poll Responses

Returns detailed information about responses to a poll, including timestamp of when the response was made and the anonymous `user-id`.

URL: `GET /poll/{pollId}/responses`

| Query Parameter | Type    | Default | Description         |
|-----------------|---------|---------|---------------------|
| option          | Integer | null    | Filter by option ID |

Expected Responses:
* `200 OK`: returns list of `response` objects
* `400 BAD REQUEST`: Poll not found
* `400 BAD REQUEST`: Option not found (this also occurs if given option does not belong to given poll)


## Application API

These endpoints are used by the application/user to query and respond to polls.
There is no authorisation/authentication on these endpoints in order to allow users to respond to polls without logging in.
To prevent a user responding multiple times, a `user-id` cookie is used to track their identity anonymously.


### Create Cookie

If the client sends a `user-id` cookie, this endpoint will do nothing and respond with nothing.
If the client does not have a `user-id` cookie however, one will be generated in the serve and sent back to the client.

URL: `POST /cookie`

Expected Responses:
* `200 OK`: no response body, but `user-id` may be present in `Set-Cookie` header


### Get Active Poll

Returns the active poll.
If the user has previous responded to this poll, vote statistics will be populated in the response body (detected via `user-id` cookie).

URL: `GET /poll`

Expected Responses:
* `200 OK`: returns active poll
* `404 NOT FOUND`: No active polls


### Respond to Poll

Submits a response to a poll. Requires that `user-id` cookie is sent in header.

URL: `PUT /{pollId}/respond/{optionId}`

Expected Responses:
* `200 OK`: returns poll with vote statistics
* `400 BAD REQUEST`: Poll not found
* `400 BAD REQUEST`: Option not found
* `403 FORBIDDEN`: User has already responded to poll


## Assumptions

* User should not need to log in to be able to access and respond to polls
* Users should not be allowed to vote in polls more than once
* Only administrators should be able to create polls
* Only one poll may be active at a time
