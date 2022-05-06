# BOCO - A rental site for everyone 

## Description
The BOCO Rental Project's purpose is to provide a solution for the increasing demand for distributed systems supporting <br> 
rental of items between members of the public. The goal of BOCO is to safely allow those who wish to lend items the means <br> 
to do so using a regulated platform meant for it; as well as to make lending easy for those who need or wish to do so. <br>
Hopefully, the solution might simultaneously help the environment in providing an easy way out for having to buy new <br>
items of necessity when the situation sees it fit. 


### Table of contents
- [Who the application is for](###Who the application is for (main target audience)) <br>
- [Database relations](###Database relation-model)
- [Technologies used](###Technologies) 
- [Application layout](###Application layout (structure))
- [Security](###Security)
- [How to run and install the backend](###How to run and install the project backend) <br>
- [How to run and install the frontend](###How to run and install the project frontend) <br>
- [How to create keys/certificates (https)](###Keys and certificates)
- [How to use the application](###How to use the application)<br>
- [Dummy data](###Dummy data)
- [CI/CD for the project](###CI/CD)<br>
- [Team members of the BOCO project](###Team members of the BOCO project)<br>
- [Future work](###Future work)<br>
- [Project license](###License)<br>

<br>

### Who the application is for (main target audience)
This application is for anyone who is interested or in need of a platform where renting is used as a solution, instead of buying. <br>
The application is currently only existing in norwegian, but future releases are planned to hold other languages as well. 

<br>

### Database relation-model 
![img_1.png](src/main/resources/images/UML.png)

<br> 

### Technologies
The BOCO Rental Project uses:
- The Vue.js Framework at Frontend
- SpringBoot to provide the backend.
- JWT - Authentication for REST APIs
- MySQL - PHPMyAdmin holds the database. 
- H2 Database Engine - In-memory database for testing
- Swagger - API documentation
- Bootstrap / Bootstrap Studio - UI design
Swagger documentation is available at **localhost:8443/swagger-ui/**

<br>

###Application layout (structure)
![img.png](src/main/resources/images/applicationLayout.png)

This application implements REST API using the layered architectural style to organize our system of code. <br>
The layered architectural-style enforces strong Separations of Concerns, which aids in: 
- Providing a consistent pattern for our application's organization (structure)
- Code readability, as code-composition is enhanced, because each layer and class deals with a single task.
- Easier modifications due to how individual each layer is, minimally impacting other layers upon change.

**The database layer** is the model folder, which holds all the entities (tables) for the database. <br>

**The persistence layer** is the repository folder, which uses JpaRepository to provide an interface for br>
accessing the database and for performing ___CRUD___ operations. <br>

**The business layer** is the service folder, which implements the core logic of the application. Our service <br>
layer is abstracted through the use of individual interfaces for each service class. The point of this interface <br>
abstraction is to lower coupling between the service layer (business layer) and the controllers. <br>

**The presentation layer** is the controller folder, which has the responsibility to handle HTTP requests <br>
through the use of endpoints. The controllers forward the requests to the Service layer; and returns the results to clients. <br>
The controllers are the only classes that communicate with the frontend.

### Security 
When frontend and backend communicate (through the controllers), JWT-tokens are used to authenticate the user roles <br>
in the database. Passwords are encrypted and salted using BCrypt password encoder (part of Spring Security). <br>
The application also uses HTTPS instead of HTTP, actively enforcing security through the use of certificates.
To use the application with HTTPS you can checkout feat/social branch. To connect with the Facebook and Google apis we 
need HTTPS. To do run the application in HTTPS we have created self certificated keystore and truststore. Google chrome
will not allow complete network requests when the certificates are not issued by certified CA's. And considering that 
running smoothly on chrome is a requirement we have not implemented this functionality in the main branch. You can access
social media login with Google and Facebook by checking out the feat/social branch.

### How to run and install the project backend
The technologies needed to run the commands are:
- Java version 11, which can be downloaded [here](https://www.oracle.com/java/technologies/downloads/#java11).
- Maven, which can be downloaded [here](https://maven.apache.org/download.cgi), and installed using [these](https://maven.apache.org/install.html) instructions. <br>
- After that, type the following in a terminal or in a command prompt to verify the installation:


    mvn --version

To clone down the project, run the following command:

    git clone https://gitlab.stud.idi.ntnu.no/hasanro/idatt2106_2022_05_backend.git

When the project has been cloned down, the pom.xml dependencies must be downloaded using the command:

    mvn dependency:resolve

After that, the package must be installed into the local repository, using the commmand:

    mvn install 

To run the application, one can choose between running through using the built-in IDE-button, or through using Maven:

    mvn spring-boot:run

<br>


### How to run and install the project frontend

The project frontend uses the Vue.js Framework. Before downloading Vue, one must have Node.js installed. <br>
Node.js installation can be found [here](https://nodejs.org/en/download/). Download the version that pertains to your operating system. <br>

Once the installer is downloaded:
- When prompted to run - click **run**.
- When welcomed by the wizard - click **next**.
- Review the license agreement and click **next** if you agree to the terms.
- The installer will prompt you for the installation location. Leave the default location, <br> unless you have a specific need to install it somewhere else – then click **next**.
- The wizard will let you select components to include or remove from the installation. <br> Choose your own desired modifications - then click **next**.
- Finally, click the **install** button to run the installer. When it finishes, click **Finish**.

Verify installation by writing into the terminal:

    node -v

Do the same for NPM:
    
    npm -v

Now that npm is installed, Vue.js can be downloaded through the use of the command:

    npm install vue

After installing Vue, the frontend project can be opened. <br>
To download all dependencies, run the command:

    npm install 

To serve up the application, run:
    
    npm run serve 

<br>

### Keys and Certificates to run on HTTPS
To run the application with social media login and HTTPS you need to check out the branch feat/social.
You need to run Mozilla Firefox and do the following:
- Type about:preferences in the address bar
- Open Advanced -> Certificates -> View Certificates -> Authorities
- Click on Import
- Locate the folder src/main/resources/keystore/keystores
- Select the rootCA.crt file and click OK
- Choose “Trust this CA to identify websites” and click OK

You also need to install the client certificate in the browser:

- Type about:preferences in the address bar in settings
- Open Advanced -> View Certificates -> Your Certificates
- Click on Import
- Locate the folder src/main/resources/keystore/keystores
- Select the clientBob.p12 file and click OK
- Input the password <password> and click OK

Turn of any ad-blocking extention and now you can run the application with HTTPS.

### Login page in branch feat/social
![img.png](src/main/resources/images/SocialLogin.png)

### How to use the application
The application is very intuitive in that it behaves the same way other applications do; 
using the same principles of Universal Design (UX).
- The user enters the login page and is prompted to either log in or register a new account. <br> The user may use Google or Facebook to register/log in if desired.
- Upon successful registration, the user is sent to the main page. 
<br> The user may choose to browse existing ads, or to create own ads if they wish to rent out items.
To have this capability the user must first verify their email address where a verification mail is sent.
- If the user wishes to browse, they may sort items based on category, price, and descending/ascending order. 
- If the user wished to rent an item, they can choose a span of available dates in the ad's calendar. 
<br> The rental is not immediate, because the owner of the item is prompted to accept the request (sent through email) before the user may borrow it.
In the messages the owner is sent a request and link with a detailed view of the rental properties, they can choose to accept or decline.
<br> Details regarding price-discussion, delivery/pick-up of the item is determined by the two parts (owner and borrower) through the use of the chat. 
- If the user wishes to chat with an arbitrary amount of people, they may do so through the use of groups. <br> Groups use WebSocket as a medium. 


### Dummy data

The application database already contains categories and dummy-data (ads, rentals, users), which are free to use. <br>
A dummy users for anyone to use when logging in is:

Username: ___andetel@stud.ntnu.no___ <br>
Password: ___passord123___

Username: ___hasano@stud.ntnu.no___ <br>
Password: ___passord123___

Username: ___maiken@gmail.com___ <br>
Password: ___passord123___

Username: ___thadsha@gmail.com___ <br>
Password: ___passord123___

### Tests
The project contains 87 (currently) backend tests, which are runnable through the use of the terminal command:

    mvn run test

The frontend tests can be run through the use of the terminal command:
    
    npm run test:unit

Both projects use GitLab as its version control system.

<br>

### CI/CD
The frontend and backend both contain their own ".gitlab-ci.yml" file. <br> The files' purpose is to control Continous Integration for the projects, 
which controls the <br> pipeline holding all jobs necessary for GitLab to run upon a developers push to main. <br>

The backend pipeline consists of the stages:
- build
- verify
- test
- pages

The frontend pipeline consists of the stages:
- build
- test

<br>


### Team members
- Hasan Rehman Omarzae
- Maiken Louise Brechan
- Anders Tellefsen
- Karoline Sund Wahl
- Eirin Svinsås
- Thadshajini Paramsothy
- Johannes Herman Havstad Morkemo
- Leonard Sandløkk Schiller
- Brage Minge

<br>

### Future work
The project has been challenging, but rewarding. It was known from the beginning that the 3 weeks spent in finishing the <br>
project would never be enough to supply the project with all the desired features. Therefore, the list of future <br>
improvements is long and detailed. 

- In the future, it would be preferable to update the application such that a user can choose between different languages. <br> The current version only supports norwegian.
- In the future, unit types (currencies) must be implemented in backend for the Ad-model (to correspond with language updates).
- When updating postal code or street address for an ad, there should be a registry checkup (norwegian registry of postal addresses) <br> to see if the address exists before the user is allowed to post an ad.
- A waiting list entity to hold borrowers in queue when more than one borrower wished to borrow an object <br> at the same date (or overlapping the same dates). This would be of great use in the event that a borrower cancels their rental (within 24 hours), <br> such that potential borrowers in queue could receive a notification by email telling them the item is available.
- A smoother way to store images in the database would be considerably helpful in saving time when fetching <br> and rendering ads from the backend to the frontend.
- All ads have a rental time of 1 year. This means that 1 year at a time is saved for each ad in the backend. <br> After that, they are deleted. This means that future functionality would be to notify the user that they have to renew their ad when it's been up for <br> ca. 11 months (like with Finn, where ads are up for 2 months at a time). 
- Currently, users can create chat groups. A future implementation could extend this functionality into more <br> of a social network, where users can follow each other.
- Possibility to report other users for indecent behaviour, scams, being bots etc.
- Remove input checks in all methods in backend because verification happens through JWT. This would make the backend faster. <br> More accurately, this would mean to remove all checks for optional-objects when fetching elements by id from JPA-repositories.
- Search methods now use “contains()” in order to search. This is inefficient and slow. <br> Future work is to implement this in a way that would make the search function faster.
- Extend ad-model, which now includes the attribute duration type, to also include durationAmount <br> (max amount of lending time) if the owner wishes to use the feature when establishing or editing an ad.
- Functionality to edit an ad in frontend.

<br>


### License

<br>
MIT License

Copyright (c) [2022] [The BOCO Team]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files 
("idatt2106_2022_05_backend" and "idatt2106_2022_05_frontend"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE



