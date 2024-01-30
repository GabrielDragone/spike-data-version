# spike-data-version
POC Skipe Version

## Anotações:
* Criei a tabela users na [V000001__create_table_users.sql](src%2Fmain%2Fresources%2Fdb%2Fmigration%2FV000001__create_table_users.sql), perceba que não tem o version.
* Ao adicionar o @Version na Entity, o sistema reclamou que não tinha a coluna version na tabela users. Tive que criar na [V000002__alter_table_users_add_column_version.sql](src%2Fmain%2Fresources%2Fdb%2Fmigration%2FV000002__alter_table_users_add_column_version.sql), isso ocorreu porque estamos usando o Flyway.
* A aplicação irá rodar sozinha a [UserService.kt](src%2Fmain%2Fkotlin%2Fbr%2Fcom%2Fgabrieldragone%2Fspikedataversion%2Fservice%2FUserService.kt), pois ela está implementando a CommandLineRunner que executa toda vez que a aplicação sobe.
* Segue o log da aplicação:
```
=====VERSION TEST BEGINS=====

newUser = User(id=null, name=Gabriel Teste 123, email=teste@email.com, version=null)
userPersisted = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=0)

userMinorVersion = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=-1)
[userMinorVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userMinorVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#22]

userBiggerVersion = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=1)
[userBiggerVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userBiggerVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#22]

userEqualVersion = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=0)
newUserEqualVersion = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=0) saved

userAfterTest = User(id=22, name=Gabriel Teste 123, email=teste@email.com, version=0)
newVersion = User(id=22, name=Gabriel Teste 123, email=new-teste@email.com, version=1)

NEW VERSION TEST BEGINS
userMinorVersion = User(id=22, name=Gabriel Teste 123, email=new-teste@email.com, version=0)
[userMinorVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userMinorVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#22]

userBiggerVersion = User(id=22, name=Gabriel Teste 123, email=new-teste@email.com, version=2)
[userBiggerVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userBiggerVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#22]

userEqualVersion = User(id=22, name=Gabriel Teste 123, email=new-teste@email.com, version=1)
newUserEqualVersion = User(id=22, name=Gabriel Teste 123, email=new-teste@email.com, version=1) saved


=====VERSION TEST END=====
```