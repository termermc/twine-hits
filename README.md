# twine-hits
Simple hit counter module for Twine

# Details
This module creates hit counter image widgets for websites and counts the amount of views the page has and how many unique IP addresses hit it.

# How to use
Clone this repository, and then inside, run `./gradlew build` (or `gradlew.bat build` on Windows).
The compiled module will be named `twine-hits-all.jar` inside of `build/libs/`.

Then, place the module inside the modules folder for Twine (2.0 or higher).

Run Twine once to generate the configuration file, then stop it. The new file will be in `configs/hits.json`.

Configure it to connect to a PostgreSQL database and then restart Twine. It should be running.

# API routes
This module does not come with a frontend, but exposes API routes. All routes are prefixed with `/api`.

The response for each route will be JSON containing a field name status. That field will be either `success` or `error`.
If it's `error`, there will be a field named `error` with more details.

## /v1/counters/create
Creates a new hit counter and returns its ID and credentials.

Params:
 - name, string up to 32 characters long
 - text_color, string containing a comma-separated RGB value (e.g. "255,0,0")
 - bg_color, same as text_color, but for background

## /v1/counter/:id/edit
Edits an existing hit counter.

Route params:
 - id, string up to 16 characters long
Params:
 - name, string up to 32 characters long
 - text_color, string containing a comma-separated RGB value (e.g. "255,0,0")
 - bg_color, same as text_color, but for background
 - password, string up to 10 characters long

## /v1/counter/:id/delete
Deletes an existing hit counter.

Route params:
 - id, string up to 16 characters long
Params:
 - password, string up to 10 characters long

## Hit counter images
Hit counter images are available at /hit/:id. If a counter with that ID does not exist, it will be an image simply displaying the text "Invalid ID".