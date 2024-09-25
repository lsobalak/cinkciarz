# Cinkciarz

## How to run:
- Use java 21
- Import project as maven using pom.xml
- Run \src\main\java\com\cinkciarz\Application.java 
- Alternatively execute src/test/java/com/cinkciarz/IntegrationTest.java

## Example usage:

### Account creation
- Username must be unique
- All fields are mandatory
- Balance must be in format ###.## while decimal places are optional
```
POST http://localhost:8080/v1/account
Content-Type: application/json

{
"username": "real_cinkciarz_666",
"firstName": "Andrzej",
"lastName": "Golota",
"initialAmountInPln": 123.11
}
```
Example response:
```
{
"API key": "s4MLDBvzHo"
}
```

### Account balance information
```
GET http://localhost:8080/v1/account?apiKey=s4MLDBvzHo
Content-Type: application/json
```
Example response:
```
{
  "username": "real_cinkciarz_666",
  "firstName": "Andrzej",
  "lastName": "Golota",
  "apiKey": "s4MLDBvzHo",
  "balances": [
    {
      "amount": 0.00,
      "currency": "DOL"
    },
    {
      "amount": 123.11,
      "currency": "PLN"
    }
  ]
}
```

### Exchange currency
- Current balance in "fromCurrency" currency must be not lower than "amount"
```
POST http://localhost:8080/v1/transaction?apiKey=s4MLDBvzHo
Content-Type: application/json

{
  "fromCurrency": "PLN",
  "toCurrency": "DOL",
  "amount": 5
}
```
Example response:
```
{
  "username": "real_cinkciarz_666",
  "firstName": "Andrzej",
  "lastName": "Golota",
  "apiKey": "s4MLDBvzHo",
  "balances": [
    {
      "amount": 1.30,
      "currency": "DOL"
    },
    {
      "amount": 118.11,
      "currency": "PLN"
    }
  ]
}
```
## TODO
- Write tests for non-positive scenarios
  - Non unique username
  - Wrong initial balance format
  - Not populate mandatory fields
  - Simulate errors with exchange rate api