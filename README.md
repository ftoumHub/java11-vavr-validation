[![Build Status](https://travis-ci.com/mtumilowicz/java11-vavr-validation.svg?branch=master)](https://travis-ci.com/mtumilowicz/java11-vavr-validation)

# java11-vavr-validation
Overview of vavr `Validation API`.

_Reference_: https://softwaremill.com/javaslang-data-validation/  
_Reference_: https://www.vavr.io/vavr-docs/#_validation  
_Reference_: https://www.baeldung.com/vavr-validation-api
_Reference_: https://blog.ippon.fr/2022/04/15/valider-des-donnees-avec-vavr/

# preface
The `Validation` control is an applicative functor (
https://softwaremill.com/applicative-functor/) 
and facilitates 
accumulating errors. When trying to compose `Monads`, the 
combination process will short circuit at the first encountered 
error. But `Validation` will continue processing the combining 
functions, accumulating all errors. This is especially useful 
when doing validation of multiple fields, say a web form, and 
you want to know all errors encountered, instead of one at a time.

Preparing `Validator` is quite straight-forward:
1. combine validations with `combine` method:
    ```
    static <E, T1, T2> Builder<E, T1, T2> combine(Validation<E, T1> validation1, Validation<E, T2> validation2)
    ```
    * up to 8 arguments
1. then use `ap` function to get requested Validator:
    ```
    public <R> Validation<Seq<E>, R> ap(Function2<T1, T2, R> f)
    ```
    * **same number of arguments as combine**

_Note that this project is strongly based on regexes (although
requires only basic knowledge). For more info about regexes
please refer my other github project: https://github.com/mtumilowicz/java11-regex_
# project description
1. suppose we want to validate `PersonRequest`:
    ```
    class PersonRequest {
        String name;
        AddressRequest address;
        List<String> emails;
        int age;
    }
    ```
    ```
    public class AddressRequest {
        String postalCode;
        String city;
    }
    ```
    with patterns:
    * `name ~ [\w]`
    * `AddressRequest`:
        * `postalCode ~ [\d{2}-\d{3}]`
        * `city ~ [\w]`
    * `emails -> all email ~ [\w._%+-]+@[\w.-]+\.[\w]{2,}`
    * `age > 0`
1. we create classes that abstract concepts mentioned above
    * age
        ```
        @Value
        public class Age {
            int age;
        
            private Age(int age) {
                this.age = age;
            }
            
            public static Age of(int age) {
                Preconditions.checkArgument(age > 0);
                
                return new Age(age);
            }
        }
        ```
    * email / emails
        ```
        @Value
        public class Email {
            public static final Predicate<String> VALIDATOR = Pattern.compile("[\\w._%+-]+@[\\w.-]+\\.[\\w]{2,}")
                    .asMatchPredicate();
        
            String email;
        
            private Email(String email) {
                this.email = email;
            }
        
            public static Email of(@NonNull String email) {
                Preconditions.checkArgument(VALIDATOR.test(email));
        
                return new Email(email);
            }
        
            public static Validation<List<String>, List<String>> validate(List<String> emails) {
                return emails.partition(VALIDATOR)
                        .apply((successes, failures) -> failures.isEmpty()
                                ? Validation.valid(successes)
                                : Validation.invalid(failures.map(email -> email + " is not a valid email!")));
            }
        }
        ```
        ```
        @Value
        public class Emails {
            List<Email> emails;
        
            public Emails(@NonNull List<Email> emails) {
                this.emails = emails;
            }
        }
        ```
    * postal code
        ```
        @Value
        public class PostalCode {
            public static final Predicate<String> VALIDATOR = Pattern.compile("\\d{2}-\\d{3}").asMatchPredicate();
            
            String postalCode;
        
            private PostalCode(String postalCode) {
                this.postalCode = postalCode;
            }
            
            public static PostalCode of(@NonNull String postalCode) {
                Preconditions.checkArgument(VALIDATOR.test(postalCode));
                
                return new PostalCode(postalCode);
            }
        
            public static Validation<String, String> validate(String postalCode) {
                return VALIDATOR.test(postalCode)
                        ? Validation.valid(postalCode)
                        : Validation.invalid(postalCode + " is not a proper postal code!");
            }
        }
        ```
    * word
        ```
        @Value
        public class Word {
            public static final Predicate<String> VALIDATOR = Pattern.compile("[\\w]+").asMatchPredicate();
            
            String word;
        
            private Word(String word) {
                this.word = word;
            }
        
            public static Word of(@NonNull String word) {
                Preconditions.checkArgument(VALIDATOR.test(word));
                
                return new Word(word);
            }
        
            public static Validation<String, String> validate(String word) {
                return VALIDATOR.test(word)
                        ? Validation.valid(word)
                        : Validation.invalid(word + " is not a proper word!");
            }
        }
        ```
1. we provide validators (some mentioned above, but here 
we get all validators together):
    * `AddressRequest`
        ```
        public class AddressRequestValidation {
            public static Validation<Seq<String>, ValidAddressRequest> validate(AddressRequest request) {
        
                return Validation
                        .combine(
                                Word.validate(request.getCity()),
                                PostalCode.validate(request.getPostalCode()))
                        .ap((city, postalCode) -> ValidAddressRequest.builder()
                                .city(Word.of(city))
                                .postalCode(PostalCode.of(postalCode))
                                .build());
            }
        }
        ```
        **note that in the end we construct** `ValidAddressRequest`:
        ```
        @Value
        @Builder
        public class ValidAddressRequest {
            PostalCode postalCode;
            Word city;
        }
        ```
    * emails
        ```
        public static final Pattern PATTERN = Pattern.compile("[\\w._%+-]+@[\\w.-]+\\.[\\w]{2,}");
        
        public static Validation<List<String>, List<String>> validate(List<String> emails) {
            return emails.partition(PATTERN.asMatchPredicate())
                    .apply((successes, failures) -> failures.isEmpty()
                            ? Validation.valid(successes)
                            : Validation.invalid(failures.map(email -> email + " is not a valid email!")));
        }
        ```
    * postal code
        ```
        public static final Pattern PATTERN = Pattern.compile("\\d{2}-\\d{3}");
        
        public static Validation<String, String> validate(String postalCode) {
            return PATTERN.matcher(postalCode).matches()
                    ? Validation.valid(postalCode)
                    : Validation.invalid(postalCode + " is not a proper postal code!");
        }
        ```
    * word
        ```
        public static final Pattern PATTERN = Pattern.compile("[\\w]+");
        
        public static Validation<String, String> validate(String word) {
            return PATTERN.matcher(word).matches()
                    ? Validation.valid(word)
                    : Validation.invalid(word + " is not a proper word!");
        }
        ```
    * positive number
        ```
        public class NumberValidation {
            public static Validation<String, Integer> positive(int number) {
                return number > 0
                        ? Validation.valid(number)
                        : Validation.invalid(number + " is not > 0");
            }
        }
        ```
    * `PersonRequestValidation`
        ```
        public class PersonRequestValidation {
            public static Validation<Seq<String>, ValidPersonRequest> validate(PersonRequest request) {
        
                return Validation
                        .combine(
                                Word.validate(request.getName()),
                                Email.validate(request.getEmails()).mapError(error -> error.mkString(", ")),
                                AddressRequestValidation.validate(request.getAddress()).mapError(error -> error.mkString(", ")),
                                NumberValidation.positive(request.getAge()))
                        .ap((name, emails, address, age) -> ValidPersonRequest.builder()
                                .name(Word.of(name))
                                .emails(emails.map(Email::of).transform(Emails::new))
                                .address(address)
                                .age(Age.of(age))
                                .build());
            }
        }
        ```
        **note that in the end we construct** `ValidPersonRequest`:
        ```
        @Builder
        @Value
        public class ValidPersonRequest {
            Word name;
            ValidAddressRequest address;
            Emails emails;
            Age age;
        }
        ```
# test
We provide full tests for this project, however here we 
will show only the most important part - `PersonRequestValidationTest`
* completely valid `PersonRequest`
    ```
    def "test validate - valid"() {
        given:
        def addressRequest = AddressRequest.builder()
                .city("Warsaw")
                .postalCode("00-001")
                .build()
        and:
        def validAddressRequest = ValidAddressRequest.builder()
                .city(Word.of("Warsaw"))
                .postalCode(PostalCode.of("00-001"))
                .build()
    
        and:
        def personRequest = PersonRequest.builder()
                .name("Michal")
                .age(1)
                .emails(List.of("michal@gmail.com"))
                .address(addressRequest)
                .build()
    
        when:
        def report = PersonRequestValidation.validate(personRequest)
    
        then:
        report.isValid()
        report.get() == ValidPersonRequest.builder()
                .name(Word.of("Michal"))
                .age(Age.of(1))
                .emails(new Emails(List.of(Email.of("michal@gmail.com"))))
                .address(validAddressRequest)
                .build()
    }
    ```
* completely invalid `PersonRequest`
    ```
    def "test validate - invalid"() {
        given:
        def addressRequest = AddressRequest.builder()
                .city("Warsaw^")
                .postalCode("a")
                .build()
    
        and:
        def personRequest = PersonRequest.builder()
                .name("Michal_")
                .age(-1)
                .emails(List.of("b"))
                .address(addressRequest)
                .build()
    
        when:
        def report = PersonRequestValidation.validate(personRequest)
    
        then:
        report.isInvalid()
        report.getError() == ["b is not a valid email!", 
                              "Warsaw^ is not a proper word!, a is not a proper postal code!", 
                              "-1 is not > 0"] as List
    }
    ```
* partially valid `PersonRequest`
    ```
    def "test validate - partially valid"() {
        given:
        def addressRequest = AddressRequest.builder()
                .city("Warsaw")
                .postalCode("00-001")
                .build()
    
        and:
        def personRequest = PersonRequest.builder()
                .name("Michal")
                .emails(List.of("b"))
                .address(addressRequest)
                .build()
    
        when:
        def report = PersonRequestValidation.validate(personRequest)
    
        then:
        report.isInvalid()
        report.getError() == ["b is not a valid email!", "0 is not > 0"] as List
    }
    ```