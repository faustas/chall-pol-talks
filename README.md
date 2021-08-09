# P&C sample task app server #

This application provides the backend service for requesting the analysis
of CSV files regarding some specific requirements.
It provides one endpoints for downloading CSV files and analyzing these regarding
the following questions.

(1) Welcher Politiker hielt im Jahr 2013 die meisten Reden?
(2) Welcher Politiker hielt die meisten Reden zum Thema ”Innere Sicherheit”?
(3) Welcher Politiker sprach insgesamt die wenigsten Wörter?

The response has the following format:

```json
{
  ”mostSpeeches”: String | null,
  “mostSecurity”: String | null,
  “leastWordy”: String | null
}
```

## System requirements ##

- Java 11

## Developer guide ##

For development and testing you need to install [sbt](http://www.scala-sbt.org/).

Start sbt
```bash
> sbt
```

Compile the application and download the required packages
```bash
> compile
```

Start the application
```bash
> run
``` 

During development you can start (and restart) the application via the `reStart`
sbt task provided by the sbt-revolver plugin.
```bash
> reStart
```

Open the app within your browser and adapt the url parameters:
```bash
http://localhost:8080/evaluation?url1=myUrl1&url2=myUrl2&url3=myUrl3
```

### Tests ###

Tests are included in the project. You can run them via the appropriate sbt task
`test`.

## What this application IS and WHAT NOT! ##

It is just a test case which solves the requested tasks provided by P&C.

For a production version, the following adaptations are the absolute minimum to add:

- Unit and Integration tests for the endpoints and used methods
- Authentication for the provided endpoints
- Check the parameters provided as url parameters

Additional adaptations could be:

- More type checks by using RefinedTypes
- Check the CSV structure
- Better error handling and feedback
- Logging
- Depending on the size of the processed CSV files 
  - Streaming
  - Statistic Lib
- ...

