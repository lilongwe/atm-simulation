# About

This is a rough and ready project that models an ATM. Done in a few hours so it is not completely implemented but does kind of show the basics of withdrawing money from an account from and ATM.

The test data is all in the tests so I have not implemented an AccountServiceFacadeImpl.

# Assumptions

1. No data access had been modeled or implemented
2. No transactions have been taken into consideration
3. Concurrency has not been taken into consideration because the would happen at transaction and account access level
4. Limited the number of dependencies as it is just a prototype - i.e. could have used things like Guava etc
5. Algorithms are working but not optimised as I don't know how they will be used
6. Concurrency in general has not been taken into consideration especially for streams and synchronising data as this could be an optimisation at a future date once the application is built
7. In the interest of expedience comments have not been added extensively not have classes been documented completely
