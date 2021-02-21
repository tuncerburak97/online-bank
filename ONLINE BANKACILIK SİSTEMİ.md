# ONLINE BANKACILIK SİSTEMİ

## Uygulamaya Giriş

   Bu projede bir online banka sisteminin backend servisleri yazılmıştır.Aşağıda temel fonksyionlar belirtilmiştir.

- **Müşteri Yönetimi**

- **Hesap Yönetimi**

- **Kart Yönetimi**

- **Transfer Yönetimi**

- **İşlem Yönetimi ve Takibi**

  

  işlemleri Java programlama dili 8 versiyonunda ve OOP prensipleri doğrultusunda uygulanmıştır.Tüm fonksiyonlar REST API'lar aracılığı ile yerine getirildi ve API'ların dökümantasyonu  swagger arayüzü ile yapılmıştır. Hata durumları uygun şekilde ele alındı ve hatalara uygun responselar kullanıcıya çıktısı gösterilecek şekilde tasarlandı.

  ​		Uygulama genelinde 

  - **Spring Boot**

  -  **Spring MVC **

  -  **JPA**

    kullanılmıştır.
    
## Uygulamanın Er Diyagramı Üzerinden Anlatılması

Uygulamanın ilk adımı olarak entity sınıfları oluşturulmuş ve aralarındaki ilişki tanımlanmıştır.Bu sınıfların ER diyagramı aşağıda görmekteyiz.

![database_schema_last](https://user-images.githubusercontent.com/33039466/107887881-4147f600-6f1a-11eb-9700-479a633aae0c.png)

​		Uygulamamızın entity sınıflarının oluşturulma sırası aşağıdaki gibidir.

- Customer 
- Address
- Contact
- Account

  - Deposit Account

  - Saving Account
- Card

  - Credit Card
  - Debit Card
- Transaction
  - Account Transaction
  - Card Transaction
  - Transfer Transaction
- System Operations

## Entityler

### Customer

​	Uygulamamızın merkezinde bulunan entity sınıfı ER diyagramı üzerinde görüldüğü gibi customer sınıfıdır.Bankacılık sistemine kayıt olmak ve işlem yapmak isteyen herkesin öncelikle müşteri kaydı yaptırması zorunludur.Müşteri kaydını başarıyla yerine getiren her kullanıcı ardından diğer işlemlere geçebilmektedir.

#### Customer sınıfı değişkenleri

- Name:Müşterinin adını tutar.
- LastName:Müşterinin soyadını tutar.
- Gender:Müşteri cinsiyetini tutar
- IdentificationNumber:Her müşterinin tekil olarak sahip olduğu kimlik numarasını tutar.
- Asset:Müşterinin hesaplarındaki toplam paranın TL karşılığını tutar.
- AddressId:Müşterinin adres bilgilerinin tutulduğu adres tablosundaki kolonun id değerini tutar
- ContactId:Müşterinin adres bilgilerinin tutulduğu adres tablosundaki kolonun id değerini tutar.
- Id:Her müşteri kolonunun veritababında tutulduğu tekil numaradır.

#### Customer sınıfı ilişkileri

- Account sınıfı ile -> OneToMany
- Credit card sınıfı ile -> OneToMany
- Transaction sınıfı ile -> OneToMany
- System Operation sınıfı ile -> OneToMany
- Address sınıfı ile -> OneToOne
- Contact sınıfı ile -> OneToOne

### Address

Müşterinin adres bilgilerinin tutulduğu sınıftır.

#### Address sınıfı değişkenleri

- id:Her address kolonunun veritababında tutulduğu tekil numaradır.
- Street:Müşterinin yaşadığı sokak bilgisini tutar.
- City:Müşterinin yaşadığı şehir bilgisini tutar.
- Zipcode:Müşterinin yaşadığı yerin zipcodenu tutar.

#### Address sınıfı ilişkileri

- Customer sınıfı ile -> OneToOne

### Contact

Müşterinin iletişim bilgilerinin tutulduğu sınıftır.

#### Contact sınıfı değişkenleri

- id:Her contact kolonunun veritababında tutulduğu tekil numaradır.
- Email:Müşterinin e-mail adres bilgisini tutar.
- PhoneNumber:Müşterinin cep telefonu bilgisini tutar.

#### Contact sınıfı ilişkileri

- Customer sınıfı ile -> OneToOne

### Account

Müşterinin sahip olduğu hesapların bilgilerinin tutulduğu sınıftır.Sistemimizde abstract class olarak tanımlanmış ve **Deposit Account** ve **Saving Account** entity classlarına kalıtım vermektedir.

#### Account sınıfı değişkenleri

- accountId:Her account kolonunun veritababında tutulduğu tekil numaradır.
- AccountNumber:Müşterinin sahip olduğu hesabın tekil numarasını tutar.
- AccountType:Müşterinin sahip olduğu hesabın tipini tutar.
- ibanNo:Müşterinin sahip olduğu hesabın tekil iban numarasını tutar.
- balance:Müşterinin sahip olduğu hesabın para miktarını tutar.
- CurrencyType:Müşterinin sahip olduğu hesabın sahip olduğu para birimi tipini tutar.

#### Account sınıfı ilişkileri

- Customer sınıfı ile -> ManyToOne

##### Deposit Account

Sistemde tanımlabilecek 2 adet hesap türünden ilkidir.Deposit account ile hem kendi hesabımıza  hem de başka hesaplara para transferi yapabiliriz.Account sınıfından kalıtım alır ve kendi alanlarında sadece Account classına ait primary key olan account_id'yi kendisi foreign key olarak tutar.

###### Deposit Account ilişkileri

- DebitCard sınıfı ile -> OneToMany

##### Saving Account

Sistemde tanımlabilecek 2 adet hesap türünden ikincisidir.Saving account ile sadece kendi hesapalrımıza para transferi yapabiliriz.Account sınıfından kalıtım alır ve kendi alanlarında sadece Account classına ait primary key olan account_id'yi kendisi foreign key olarak tutar.

### Card

Müşterinin kart bilgilerinin tutulduğu sınıftır.Sistemimizde abstract class olarak tanımlanmış ve **Credit Card** ve **Debit Card** entity classlarına kalıtım vermektedir.

#### Card sınıfı değişkenleri

- card_id:Her card kolonunun veritababında tutulduğu tekil numaradır.
- cardNumber:Kartların 16 haneli tekil numarasını tutar.
- cardType:Kartların sahip olabileceği tipi tutar.
  - DebitCard
  - CreditCard
- isUsable:Kartın geçerli olup olmadığı bilgisini tutar.
- securityNumber:Kartın 3 haneli güvenlik numarasını tutar.
- expiredDate:Kartın geçerlilik süresini tutar.

##### Credit Card

Card sınıfından kalıtım alır.Kalıtım türü TABLE_PER_CLASS olduğu için kartın alanlarıda bulunan değişkenlere o da sahip olur.

###### Credit Card sınıfı değişkenleri

Bu değişkenlere ek olarak aşağıdaki değişkenleri tutar:

- cardLimit:Kartın sahip olduğu toplam limiti tutar.
- currentLimit:Kartın güncel olarak harcandıktan sonraki limitini tutar.

###### Credit Card ilişkileri

- Customer sınıfı ile -> ManyToOne

##### Debit Card

Card sınıfından kalıtım alır.Kalıtım türü TABLE_PER_CLASS olduğu için kartın alanlarıda bulunan değişkenlere o da sahip olur.

###### Debit Card ilişkileri

- DepositAccount sınıfı ile -> ManyToOne

### Transaction

Müşterilerin kendi hesaplarında veya birbirlerinin hesapları arasında yapılan transfer işlemleri,kredi kartı ve debit kartından yapılan işlemleri ve kredi kart borcu öderken gerçekleşen işlemleri yöneten ve veritabanında kayıt altına alan entity sınıfır.**AccountTransaction**,**TransferTransaction** ve **CardTransaction** sınıflarına kalıtım verir.Kalıtım türü JOINED'dır.

##### Transaction sınıfı değişkenleri

- tarnsactionId:Her transaction kolonunun veritababında tutulduğu tekil numaradır.


##### Transaction sınıfı ilişkileri

- Customer sınıfı ile -> ManyToOne

##### Account Transaction 

Sistemde tanımlabilecek 3 adet transaction türünden ilkidir.Hesap üzerinden yapılan para ekleme ve para çıkarma işlemlerini yönetir.

###### Account Transaction sınıfı değişkenleri

- accountNumber:İşlemin yapılacağı hesabın numarasını tutar.
- currencyType:İşlemin yapılacağı hesabın para birimini tutar.
- amount:İşlemde kullanılacak ücret miktarını tutar.
- transactionType:İşlemin tipini tutar.
- date:İşlemin yapıldığı tarihi tutar.


##### Card Transaction 

Sistemde tanımlabilecek 3 adet transaction türünden ikincisidir.Kart üzerinden yapılan para ekleme ve para harcama ve kredi kartı borcu ödeme işlemlerini yönetir.

###### Card Transaction sınıfı değişkenleri

- cardNo:İşlemin yapılacağı kartın numarasını tutar.
- currencyType:İşlemin yapılacağı kartın bağlı olduğu hesabın para birimini tutar.
- amount:İşlemde kullanılacak ücret miktarını tutar.
- transactionCardType:İşlemde kullanılacak kart tipini tutar.
- date:İşlemin yapıldığı tarihi tutar.
- transactionType:İşlemin tipini tutar.

##### Transfer Transaction 

Sistemde tanımlabilecek 3 adet transaction türünden sonuncusudur.Hesaplar arası yapılan işlemleri yönetir.

###### Transfer Transaction sınıfı değişkenleri

- fromIban:Gönderici iban umarasınını tutar.
- toIban:Alıcı iban umarasınını tutar.
- fromCurrencyType:Gönderici hesap para birimini tutar.
- toCurrencyType:Alıcı hesap para birimini tutar.
- amount:İşlemde kullanılacak ücret miktarını tutar.
- transactionType:Gönderici hesap türü ile alıcı hesap türünü tutar.
- date:İşlemin yapıldığı tarihi tutar.

### Operation

Sistemdeki müşterilerin,hesapların ve kartların silinmesi,oluşturulması ve güncelleştirilme bilgilerini tutar.

#### Operation sınıfı değişkenleri

- operationType:İşlemin tipini tutar.
- operationTime:İşlemin zamanını tutar.


#### Operation sınıfı ilişkileri

- Customer sınıfı ile -> ManyToOne

## Repositoryler

Repositorylerin kullanım amacı entity sınıflarının veritabanına kaydedilmesidir.Repository sınıfları aşağıda belirtilmiştir.

### - Customer Repository
### - System Operations Repository
### - Account  Repository
#### Deposit Account Repository
#### Saving Account Repository
### - Card Repository
#### Credit Card Repository
#### Debit Card Repository
### - Transaction Repository
#### Account Transaction Repository 
#### Card Transaction Repository
#### Transfer Transaction Repository

## Service

   Servisler uygulamanın business katmanıdır.Bu katmanda tanıtım bölümünde anlattığımız online bankacılık sistemimizin fonksiyonlarının nasıl ve hangi metodlarla yapıldığını yazacağız.Bu katmanda yazılan kodlar uygulamanın nasıl çalışacağı gerektiğine karar verir ve verimlilik ve SOLID prensipleri esas alınır.Sırasıyla servislerimize ve kullandıkları metodlara göz atalım.

### Customer Service

   Bu servisimizde müşteri oluşturma,silme,güncelleme ve listeleme işlemleri yapılmaktadır.Öncelikle bir CustomerService interface'i oluşturulur ve kullanılacak metodlar içine yazılır.Silme,oluşturma ve güncelleme işlemlerimizin dönüş tipi ResponseEntity olarak yapıldı.Bunun nedeni gerekli cevapların kullanıcıya anlaşılır ve kolay bir şekilde iletilmesidir.

`CustomerService.java`

```java
public interface CustomerService {

    ResponseEntity<Object> create(CreateCustomerRequest request) throws Exception;
    List<Customer> findAll();
    ResponseEntity<Object> deleteCustomer(long id);
    Customer findByIdentificationNumber(String idNumber);
    ResponseEntity<Object> updateEmail(UpdateEmail email, long id);
    ResponseEntity<Object> updatePhoneNumber(UpdatePhoneNumber number, long id);
    ResponseEntity<Object> updateAddress(UpdateAddress address, long id);

}
```

​    Ardından bu interface'i implemente eden bir CustomerServiceImpl sınıfı oluşturulur ve metodlar override edilerek body kısımları gerekli kod parçaları ile doldurulur.Bu sınıfımızın bir servis olduğunu @Service anatasyonu ile belirtiriz.Bu sınıfımızı kullanırken müşteri işlemlerini kaydetmek için Customer ve SystemOperation repositoryleri @Autowired anatosyonu ile sınıfımıza enjekte edilir.

`CustomerServiceImpl.java`

```java
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SystemOperationsRepository systemOperationsRepository;


    public CustomerServiceImpl(CustomerRepository customerRepository,SystemOperationsRepository systemOperationsRepository) {
        this.customerRepository = customerRepository;
        this.systemOperationsRepository = systemOperationsRepository;
    }

    public CustomerServiceImpl() {

    }
```

   Müşteriyi sisteme kaydederken belirli kuralları göz ardı etmemek durumundayız.Bu çerçevede bu kuralların neler olduğunu ve bizim uygulamımızda nasıl önlem aldığımızı aşağıda belirtelim;

- E-mail tipi geçerli olmalı ve sistemde aynı e-mail'le başka bir kullanıcı olmamalı.Geçerli e-mail tipi için boolen dönüş tipinde bir fonksiyon yazıldı.Eğer şartlara uyuyorsa true,uymuyorsa false olarak dönüş yapıyor.

  

  `CustomerServiceImpl.java`

  ```java
  boolean emailChecker(String email){
  
      String regex = "^(.+)@(.+)$";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(email);
  
      return matcher.matches();
  
  }
  ```

- Yukarıda görüldüğü gibi metodumuz geçerlilik durumuna göre bir değer dönüyor.Bu değeri biz create metodumuzda boolean bir değişkene atıyoruz ve false olması durumunda kullanıcıya gerekli mesaj döndürülüyor.
- Diğer bir önemli konu ise e-mail,identificationNumber ve phoneNumber değişkenlerinin veritabanında tekil olarak bulunması koşulu.Bu nedenle boolean metodlar yardımı ile input olarak girilen değişken veritabanında bulunan herhangi bir kullanıcıya ait mi sorgusu yapılır ve sonucun null olması durumunda fonksiyon true olarak dönüş yapar.

``CustomerServiceImpl.java``

```java
public boolean identificationNumberChecker(String idNumber){

    Customer idNumberChecker =customerRepository.findByIdentificationNumber(idNumber);

    if(idNumberChecker!=null){
        return false;
    }

    return true;
}
```

- Create metodundan örnek bir kod parçası:

`CustomerServiceImpl.java`

```java
boolean identificationNumberCheck=this.identificationNumberChecker(request.getIdentificationNumber());
if(!identificationNumberCheck)
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("A customer with this identification number was found in the records");
```

- Veritabanından bir müşteri silenecekken dikkat edilmesi gereken hususlar ise müşterinin herhangi bir kredi borcunun olup olmaması ve hesaplarında para bulunma durumudur.Eğer müşterinin borcu varsa veya hesaplarında para varsa silme işlemine izin verilmez.Delete fonksiyonumuza bu durumlar için yazılan kod parçacığı aşağıdadır.

`CustomerServiceImpl.java`

```java
if(customer.getAsset()!=0){
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer have a asset.Deletion not allowed");
}

for(int i=0;i<customer.getCreditCards().size();i++){

    double debt = customer.getCreditCards().get(i).getCardLimit()- customer.getCreditCards().get(i).getCurrentLimit();

    if(debt!=0){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Customer have a credit debt.Deletion not allowed");
    }
}
```

### Account Service

   Bu servisimizde ise hesap oluşturma ve hesap silme işlemleri yapılmıştır.Fakat bu noktada karşılaşılan çok önemli bir problem bulunmaktadır.Hesaplar oluşturulurken veya silinirken sadece hesap türü farklıdır ve onun dışında yapılan işlemler tamamiyle aynıdır.Bu durumda kod tekrarı fazlaca meydana gelebilir ve uygulamamız verimsiz olabilmektedir.Bu nedenle çözüm olarak Generics metodlar kullanılmıştır.Her iki hesap türününde kalıtım aldığı ata sınıfın Account sınıfı olduğu bilinmektedir.Parametre olarak T tipinde bir değişken kullanılıp T değerine göre hesap değişkeni ataması fonksiyon içinde yapılmıştır.Hep birlikte önce AccountService Interface'ine ve ardından da bu servisi implemente eden AccountServiceImpl sınıfına bakalım.



`AccountService.java`

```java
public interface AccountService {

    public  <T extends Account> ResponseEntity<Object> createAccount(T a, CreateAccountRequest request);
    public <T extends Account> ResponseEntity<Object> deleteAccount(T a, String accountNumber);
}
```



``AccountServiceImpl.java`

```Java
@Override
public <T extends Account> ResponseEntity<Object> createAccount(T a, CreateAccountRequest request) {

    Customer customer = customerRepository.findById(request.getCustomerId());

    if (customer == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
    }

    Account account = null;
    AccountRepository accountRepository = null;

    if(!(request.getCurrencyType().equals("USD")||request.getCurrencyType().equals("TRY")||request.getCurrencyType().equals("EUR"))){

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Account type must be USD,EUR or TRY");
    }


    if (a instanceof DepositAccount) {

        account = new DepositAccount();
        accountRepository = depositAccountRepository;
    }
    if (a instanceof SavingAccount) {

        account = new SavingAccount();
        accountRepository = savingAccountRepository;
    }
```

- Görüldüğü üzere T parametrem deposit account sınıfının bir instance'sı ise Deposit Account ve Deposit Repository,saving account sınıfının bir instance'sı ise aynı şekilde Saving Account ve Saving Repository tanımlamaları yapılır ve kod tekrarından kaçınılmış olur.
- Bir diğer problem ise bu hesaplara ait hesap numarası ve iban numarası değerlerinin üretilmesi ve bu değerlerin tekil olmasıdır.Bu duruma ait çözüm ise şöyledir:

``

```java
boolean isUnique=true;

while (isUnique){

    AccountNumberGenerator accountNumberGenerator=new AccountNumberGenerator();
    String accountNumber=accountNumberGenerator.generateAccountNumber();

    if(accountRepository.findByAccountNumber(accountNumber)!=null){
        continue;
    }

    IbanNumberGenerator ibanNumberGenerator = new IbanNumberGenerator();
    String ibanNo=ibanNumberGenerator.ibanGenerate(accountNumber);

    if(accountRepository.findByIbanNo(ibanNo)!=null){
        continue;
    }
    //Hesap ve müşteri repolarına save işlemi
    //System Operation reposuna yapılan işlemin kayıt edilmesi
    isUnique=false;
```

- Burada numaraları üretirken generator paketi altında bulunan AccountNumberGenerator ve IbanNumberGenarator sınıfları kullanıldı.Boolean bir değişken numaraların tekil olduğuna karar verilene kadar true değerine atandı.Numaralar tekilse false değerine eşitlendi ve döngüden çıkılıp kayıt işlemleri yapıldı.
- Hesap oluşturma ve hesap silme işlemleri deposit ve saving accounta ait servislerde ayrı ayrı implemente edildi:

`DepositAccountServiceImpl.java`

```java
@Override
public ResponseEntity<Object> createAccount(CreateAccountRequest request){
    DepositAccount depositAccount = new DepositAccount();
    return accountService.createAccount(depositAccount,request);
}
```

`SavingAccountServiceImp.java`

```java
@Override
public ResponseEntity<Object> deleteAccount(String accountNumber) {
    SavingAccount savingAccount = new SavingAccount();
    return accountService.deleteAccount(savingAccount,accountNumber);
}
```

### Card Service

   Bu servisimizde kartları oluşturma işlemlerini yapıyoruz.

`CardService.java`

```
public interface CardService {

    public ResponseEntity<Object> createCreditCard(CreditCard creditCard, CreateCreditCardRequest request);
    public ResponseEntity<Object> createDebitCard(DebitCard debitCard, CreateDebitCardRequest request);
}
```

- Kartlarımızı oluştururken ise kritik bir kaç işlem var.Bunlardan ilki kart numarasının tekil olması ve ikinciside kartların son geçerlilik tarihleri ve güvenlik numaralarının belirlenmesi.Kart numarası oluştururken generator paketimizde bulunan CardNumberGenerator sınıfımızı kullanıyoruz ve tekilliği için yine while döngüleri ile çalışıyoruz.

`CardServiceImpl.java`

```Java
boolean isUnique=true;
while (isUnique){

    String cardNumber = cardNumberGenerator.generateCardNumber();

    if(creditCardRepository.findByCardNumber(cardNumber)!=null){
        continue;
    }

    creditCard.setCardNumber(cardNumber);
    isUnique=false;
}
```

- Kartımızın güvenlik numarası için generator paketimizden SecurityCodeGenerator sınıfımızı kullanıyoruz.Son kullanma tarihi içinse CardServiceImpl sınıfımızda tanımladıgımız expiredDate metodu bize yardımcı oluyor.

`CardServiceImpl.java`

```Java
public String expiredDate(){

    Calendar calendar = Calendar.getInstance();
    int month = calendar.get(Calendar.MONTH)+1;
    int year = calendar.get(Calendar.YEAR)+5;
    String expiredDate= month +"/"+ year;

    return expiredDate;

}
```

- Peki ürettiğimiz kartların geçerlilik tarihlerini nasıl kontrol edeceğiz.Yukarıdaki metod sayesinde kartlar 5 yıl boyunca üretildiği aya kadar geçerliliğini koruyor.Peki biz geçerliliği nasıl kontrol edeceğiz.Bu noktada Spring Framework'un bize sağladığı önemli bir anatosyon işimizi görmektedir.

`CardServiceImpl.java`

```java
@Scheduled(cron = "0 0 1 * *")
public void isUsable(){


    Calendar calendar = Calendar.getInstance();
    int month =calendar.get(Calendar.MONTH)+1;
    int year = calendar.get(Calendar.YEAR)+1;

    String montString = String.valueOf(month);
    String yearString = String.valueOf(year);

    List<Card> creditCards=creditCardRepository.findAll();

    for (Card creditCard : creditCards) {

        if (creditCard.getExpiredDate().equals(montString+"/"+yearString)) {

            creditCard.setUsable(false);
            creditCardRepository.save(creditCard);
        }

    }
}
```

- Son olarak ise nasıl kart oluşturdugumuza bakalım.

`CreditCardServiceImpl.java`

```java
@Override
public ResponseEntity<Object> createCredit(CreateCreditCardRequest request) {

    CreditCard creditCard = new CreditCard();
    return cardService.createCreditCard(creditCard,request);
}
```



### Transaction Service

   Tranaction service uygulamadaki para işlemlerinin yönetildiği kısımdır.Bu servisimiz 3 farklı parçaya ayrılmıştır:

- AccountTransactionService:Hesap ile ilgili para işlemlerinin yapıldığı servis.
- CardTransactionService:Kart ile ilgili para işlemlerinin yapıldığı servis.
- TransferTransactionService:Hesaplararası para transferlerinin yapıldığı servis.

#### AccountTransactionService

   Bu servisimizde hesap üzerinden para aktarma,para harcama ve hesap üzerinden kredi kartı borcu ödeme fonksiyonları tanımlanmıştır.Yukarıda olduğu gibi hesap işlemlerinden generic metodlardan faydanılmış ve kod maliyeti azaltılmıştır.Önce interfacemize ardından bu interface'i implemente eden servisimize göz atalım.

`AccountTransactionService.java`

```java
public interface AccountTransactionService {

    public <T extends Account> ResponseEntity<Object> balanceOnAccount(T a, String transactionType, AccountTransactionRequest request) throws IOException;
    public <T extends Account> ResponseEntity<Object> debtOnAccount(T a, DebtOnAccountRequest request) throws IOException;
}
```

`AccountTransactionServiceImpl.java`

```java
@Override
public <T extends Account> ResponseEntity<Object> balanceOnAccount(T a, String transactionType, AccountTransactionRequest request) throws IOException {

    Account account=null;
    AccountRepository repository=null;

    if(a instanceof DepositAccount){

        account=depositAccountRepository.findByAccountNumber(request.getAccountNumber());
        repository=depositAccountRepository;

        if(account==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
    }

    if(a instanceof SavingAccount){

        account=savingAccountRepository.findByAccountNumber(request.getAccountNumber());
        repository=savingAccountRepository;

        if(account==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

    }
```

- Yukarıda görüldüğü üzere parametre olarak yine account tipi belirtiliyor ve TransactionType ile işlemin Para Ekleme veya Para Harcama mı olduğu giriliyor.Ardından request paketi altındaki sınıfımız olan AccountTransactionRequest ile Hesap numarası ve kullanılacak para miktarı alınıyor.
- Bu aşamadan sonra her transaction işlemini yakından ilgilendiren önemli bir olay var.Bildiğiniz üzere 3 farklı para birimi bulunuyor ve bir api aracılığı ile bu birimlerin dönüşüm oranları alınıp uygun şekilde işlenip dönüşüm yapılıyor.Dönüşüm sonucunda hesap para birimi oranında kullanıcının toplam varlığına TL tipinde ekleme veya çıkarma yapılması lazım.Bu işlemlerin nasıl yapıldığına yakından bakalım.

`CurrencyOperation.java`

```java
public class CurrencyOperation {

    @JsonProperty("date")
    private Date date;
    @JsonProperty("base")
    private String base;
    @JsonProperty("rates")
    private Map<String, Double> rates;
}
```

- Apide bulunan değişkenlerin tipinde projemizde bir sınıf oluşturuyoruz ve sınıfın değişken değerlerini api değerleri ile eşleşecek şekilde map'liyoruz.Ardından transaction işleminin yapıldığı sınıfa geri dönüyoruz.

``AccountTransactionServiceImpl.java``

```java
ObjectMapper objectMapper =new ObjectMapper();
URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

```

- Object mapper nesnesin readValue metodu ile belirtilen urldeki değerleri classımıza map ediyoruz ve bu şekilde oranlarımız artık sisteme dahil oluyor.Ardından hesabın para birimini parametre olarak girilen amount değerine bölüp müşteri ana varlığına ekleme veya çıkarma yapıyoruz.

```AccountTransactionServiceImpl.java```

```java
Customer customer =customerRepository.findById(account.getCustomer().getId());
double newReceiverCustomerAsset = request.getAmount()/currencyClass.getRates().get(account.getCurrencyType().toString());

	if(transactionType.equals("WithdrawBalance")){

   	 	if(account.getBalance()>=request.getAmount()){

        account.setBalance(account.getBalance()-request.getAmount());
        customer.setAsset(customer.getAsset()-newReceiverCustomerAsset);

        repository.save(account);
        customerRepository.save(customer);

        AccountTransaction accountTransaction = new AccountTransaction(request.getAccountNumber(),
                account.getCurrencyType().toString(),
                request.getAmount(),"WithdrawBalance",
                new Timestamp(System.currentTimeMillis()));

        accountTransactionRepository.save(accountTransaction);

        return ResponseEntity.status(HttpStatus.OK).body("Balance is withdrawed");
    }
}
```

- Örnek olarak hesaptan para çekme işlemi yapıldıysa hesapta yeterli para olması durumunda hesaptan o birim kadar para çekilir ve tl oranında müşteri ana varlığından düşülür.Bu işlemleri DepositAccount ve Saving Account'a ait servislerde gösterelim.

`DepositAccountServiceImpl.java`

```java
@Override
public ResponseEntity<Object> addBalance(AccountTransactionRequest request) throws IOException {

    DepositAccount depositAccount = new DepositAccount();
    String type="AddBalance";
    return accountTransactionService.balanceOnAccount(depositAccount,type,request);


}
```

`SavingAccountServiceImpl.java`

```java
@Override
public ResponseEntity<Object> withdrawBalance(AccountTransactionRequest request) throws IOException {
    SavingAccount savingAccount = new SavingAccount();
    String type="WithdrawBalance";
    return accountTransactionService.balanceOnAccount(savingAccount,type,request);
}
```

   Bu serviste bir diğer önemli fonksiyonumuz ise kredi kartı borcunu hesap üzerinden ödeme.Kredi kartı borcu tl üzerinden ödeniyor ve seçilen hesabın para birimi yine api üzerinden gerekli oranların alınıp kontrol edilmesi ile oluyor.Eğer yeterli para varsa borç ödeniyor ve kartın güncel limiti ödenilen borç kadar artarken,müşteri ana hesabı borç kadar azaltılıp,hesabında mevcut parası,hesap para birimi kadar arttırlıyor.

`AccountTransactionServiceImpl.java`

```Java
@Override
public <T extends Account> ResponseEntity<Object> debtOnAccount(T a, DebtOnAccountRequest request) throws IOException {
```



`AccountTransactionServiceImpl.java`

```Java
//hesap-müşteri eşlemesi
if(account.getCustomer()!=creditCard.getCustomer()){

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer can pay only own credit card debt");
}
```



`AccountTransactionServiceImpl.java`

```Java
//hesapta yeterli paranın bulunmaması
double currencyAsset =account.getBalance()/currencyClass.getRates()
        .get(account.getCurrencyType().toString());

if(currencyAsset<request.getDebt()){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient balance");
}
```

`AccountTransactionServiceImpl.java`

```Java
//hesapta yeterli paranın bulunması
creditCard.setCurrentLimit(creditCard.getCurrentLimit()+request.getDebt());

account.setBalance(account.getBalance()- (request.getDebt()*currencyClass.getRates().get(account.getCurrencyType().toString())));

customer.setAsset(customer.getAsset()-request.getDebt());
```

- Örnek olarak DepositAccount ve SavingAccount servislerinde kodun uygulanmasına bakalım.

`DepositAccountServiceImpl.java`

```Java
@Override
public ResponseEntity<Object> withdrawBalance(AccountTransactionRequest request) throws IOException {
    DepositAccount depositAccount = new DepositAccount();
    String type="WithdrawBalance";
    return accountTransactionService.balanceOnAccount(depositAccount,type,request);
}
```

#### CardTransactionService

   Kart hesap işlemleri burada yapılır.Bu işlemler sırasıyla:

- Kredi kartı alış-verişi
- Debit Kart alış-verişi
- Debit kart üzerinden borç ödeme

`CardTransactionService.java`

```java
public interface CardTransactionService {

    public ResponseEntity<Object> withdrawCreditCard(CardTransactionRequest transaction);

    public ResponseEntity<Object> debitCardTransaction(CardTransactionRequest transaction,String transactionType) throws IOException;

    public <T extends Account> ResponseEntity<Object> debtOnCard(T a, DebtOnCardRequest request) throws IOException;


}
```

- Kredi kartı harcama işlemi request paketinde bulunan CardTransactionRequest sınıfı parametre alınarak yapılır.Girilen kart sistemde mevcutsa ve yeteri kadar limit varsa işlem geçekleştirilir.
- Debit kart harcama ise debit kart varsa,debit kartın bağlı bulunduğu hesabın para birimi kadar harcama yapılarak yapılır.Hesapta yeteri kadar para yoksa işlem reddedilir.
- Debit kart üzerinden borç ödeme işlemi ise daha kritik bir kısımdır.Girilen hesap parametresine göre debit kart mevcutsa kredi kartının ödenecek borcu debit kartın bağlı olduğu hesabın para birimine çevrilir.Yeterli para varsa işlem gerçekleşir.

`CardTransactionServiceImpl.java`

```java
@Override
public <T extends Account> ResponseEntity<Object> debtOnCard(T a, DebtOnCardRequest request) throws IOException {

    Account account = null;
    AccountRepository accountRepository=null;

    CreditCard creditCard = creditCardRepository.findByCardNumber(request.getCreditCardNumber());
    DebitCard debitCard = debitCardRepository.findByCardNumber(request.getCardNumber());

    if(creditCard==null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credit card not found");
    }
    if(debitCard==null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found");
    }
    if(a instanceof DepositAccount){

        account=debitCard.getDepositAccount();
        accountRepository=depositAccountRepository;

    }


    ObjectMapper objectMapper =new ObjectMapper();
    URL url = new URL("https://api.exchangeratesapi.io/latest?base=TRY");
    CurrencyOperation currencyClass =objectMapper.readValue(url,CurrencyOperation.class);

    Customer customer = account.getCustomer();

    double currencyAsset =account.getBalance()/currencyClass.getRates()
            .get(account.getCurrencyType().toString());

    if(currencyAsset<request.getDebt()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient balance");
    }

    creditCard.setCurrentLimit(creditCard.getCurrentLimit()+request.getDebt());

    account.setBalance(account.getBalance()- (request.getDebt()*currencyClass.getRates().get(account.getCurrencyType().toString())));

    customer.setAsset(customer.getAsset()-request.getDebt());

    accountRepository.save(account);
    creditCardRepository.save(creditCard);
    customerRepository.save(customer);


  CardTransaction debitCardTransaction = new CardTransaction(request.getDebt(),"DebtPayment",new     	Timestamp(System.currentTimeMillis()),debitCard.getCardNumber(),"DebitCard",debitCard.getDepositAccount().getCurrencyType().toString());
  debitCardTransaction.setCustomer(account.getCustomer());
  cardTransactionRepository.save(debitCardTransaction);

  CardTransaction creditCardTransaction = new CardTransaction(request.getDebt()/currencyClass.getRates().get(account.getCurrencyType().toString()),"DebtPayment",new Timestamp(System.currentTimeMillis()),debitCard.getCardNumber(),"CreditCard","TRY");
  creditCardTransaction.setCustomer(account.getCustomer());
  cardTransactionRepository.save(creditCardTransaction);


    return ResponseEntity.status(HttpStatus.OK).body("Credit card debt amounting to "+request.getDebt()+" TL has been paid");

}
```

- Kredi kartı servisinde bu işlemin nasıl çalıştığına bakalım.

`CreditCardServiceImpl.java`

```java
@Override
public ResponseEntity<Object> debtOnDepositAccount(DebtOnAccountRequest request) throws IOException {

    DepositAccount depositAccount =new DepositAccount();
    return accountTransactionService.debtOnAccount(depositAccount,request);
}
```

#### TransferTransactionService

   Bu servisimizde ise hesaplar arası para transferi işlemleri yapılır.Gönderen hesabın para birimi api ile alıcı hesabın para birimine çevrilir ve koşullar sağlanıyorsa işlem tamamlanır.Yine kod karmaşasını önlemek için generic metodlardan faydanılmıştır.

`TransferTransactionService.java`

```java
public interface TransferTransactionService {

    public <T extends Account> ResponseEntity<Object> sendMoney(T a, T b, TransferTransactionRequest request) throws IOException;


}
```



`TransferTransactionServiceImpl.java`

```java
  if(a instanceof SavingAccount && b instanceof DepositAccount){

            senderAccount=savingAccountRepository.findByIbanNo(request.getFromIbanNo());
            receiverAccount=depositAccountRepository.findByIbanNo(request.getToIbanNo());

            if(senderAccount==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender account not found");
            }
            if(receiverAccount==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Receiver account not found");
            }

            if(!(senderAccount.getCustomer().equals(receiverAccount.getCustomer()))){

           return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("You can send balance to just  your own account with your saving account");
            }

            senderRepo =savingAccountRepository;
            receiverRepo =depositAccountRepository;

        }
```

`TransferTransactionServiceImpl.java`

```java
  String senderType =senderAccount.getCurrencyType().toString();
  String receiverType =receiverAccount.getCurrencyType().toString();

  double transactionRate = currencyClass.getRates().get(receiverType) /currencyClass.getRates().get(senderType) ;

  double newSenderBalance = senderAccount.getBalance()-request.getAmount();
  double newReceiverBalance =receiverAccount.getBalance()+ (request.getAmount()*transactionRate);

  double newSenderCustomerAsset = request.getAmount()/currencyClass.getRates().get(senderType);
  double newReceiverCustomerAsset = request.getAmount()/currencyClass.getRates().get(senderType);
```

- Para transferi işleminin nasıl uygulandığına SavingAccountServisinden bakalım.

`SavingAccountServiceImpl.java`

```Java
@Override
public ResponseEntity<Object> sendMoneyToDeposit(TransferTransactionRequest request)throws IOException {

    SavingAccount sender = new SavingAccount();
    DepositAccount receiver = new DepositAccount();

    return transactionService.sendMoney(sender,receiver,request);

}
```

 

## Controller

   Controllerımız api ile haberleşme yaptığımız kısımdır.Swagger arayüzü ile oluşturulan controllerlarımıza göz atalım.

![swagger_1](https://user-images.githubusercontent.com/33039466/107887947-c29f8880-6f1a-11eb-80f8-c20df673714b.PNG)

- 5 adet controllerımız mevcuttur.
- Customer Controller

![swagger_2](https://user-images.githubusercontent.com/33039466/107887949-cc28f080-6f1a-11eb-9c1f-316b7b18a074.PNG)

- Deposit Account Controller

![swagger_3](https://user-images.githubusercontent.com/33039466/107887953-d77c1c00-6f1a-11eb-8d08-052767cce6c0.PNG)

- Saving Account Controller

![swagger_4](https://user-images.githubusercontent.com/33039466/107887955-d814b280-6f1a-11eb-9ca2-d7e6f9c5be1b.PNG)

- Credit Card Controller

![swagger_5](https://user-images.githubusercontent.com/33039466/107887956-d814b280-6f1a-11eb-8cfe-fcde7a2392cb.PNG)

- Debit Card Controller

![swagger_6](https://user-images.githubusercontent.com/33039466/107887957-d8ad4900-6f1a-11eb-94ec-4a2b3f892fab.PNG)

### Examples

![swagger_7](https://user-images.githubusercontent.com/33039466/107887958-d8ad4900-6f1a-11eb-869f-830295434e2b.PNG)

![swagger_8](https://user-images.githubusercontent.com/33039466/107887959-d945df80-6f1a-11eb-9b43-ad387d048c34.PNG)

![swagger_9](https://user-images.githubusercontent.com/33039466/107887960-d945df80-6f1a-11eb-94d3-9131fad66249.PNG)

![swagger_10](https://user-images.githubusercontent.com/33039466/107887961-d9de7600-6f1a-11eb-8448-71075d72808c.PNG)

![swagger_11](https://user-images.githubusercontent.com/33039466/107887962-d9de7600-6f1a-11eb-92c5-1a422cfbf743.PNG)

![swagger_12](https://user-images.githubusercontent.com/33039466/107887963-da770c80-6f1a-11eb-9c76-3c55f06005df.PNG)

![swagger_13](https://user-images.githubusercontent.com/33039466/107887964-da770c80-6f1a-11eb-80fa-e681d5cb16de.PNG)

![swagger_14](https://user-images.githubusercontent.com/33039466/107887965-da770c80-6f1a-11eb-9d00-2848e5212992.PNG)
