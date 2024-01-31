# spike-data-version
POC Skipe Version

## Anotações:
* Criei a tabela users na [V000001__create_table_users.sql](src%2Fmain%2Fresources%2Fdb%2Fmigration%2FV000001__create_table_users.sql), perceba que não tem o version.
* Ao adicionar o @Version na Entity, o sistema reclamou que não tinha a coluna version na tabela users. Tive que criar na [V000002__alter_table_users_add_column_version.sql](src%2Fmain%2Fresources%2Fdb%2Fmigration%2FV000002__alter_table_users_add_column_version.sql), isso ocorreu porque estamos usando o Flyway.
* A aplicação irá rodar sozinha a [UserService.kt](src%2Fmain%2Fkotlin%2Fbr%2Fcom%2Fgabrieldragone%2Fspikedataversion%2Fservice%2FUserService.kt), pois ela está implementando a CommandLineRunner que executa toda vez que a aplicação sobe.
* O registro só é salvo quando a versão é igual a do banco, caso contrário, é lançado uma exceção.
* Mesmo se forçarmos a versão, o registro não é salvo, pois a versão do banco é maior ou menor, dependendo da forma como for forçada.
* O incremento da versão é feito automaticamente pelo Hibernate quando há alguma alteração na entidade. Se salvar a entidade exatamente como ela está, a versão não é incrementada.
* Realizamos testes simulando a User (com a version, que seria utilizada na Lib Update) e a UserWithouVersion (sem a version, que seria utilizada no Core).
  * O campo version deve ser criada com DEFAULT 1, para atribuir esse valor a todos os registros já existentes.
  * No User (com a version), o campo deve ser instanciado com valor DEFAULT 1.
  * Quando for salvar a entidade do UserWithoutVersion não será passada a versão, pois não deverá existir esse campo na entidade e deverá salvar normalmente.
  * Já no User (com a version da lib), deverá ser passada para validar se a versão é a mesma que está no banco, caso contrário, lançar uma exceção.
* Nesse projeto, também realizamos os testes a respeito das Anotações @CreatedDate e @LastModifiedDate, seguem explicações:
  * @EntityListeners(AuditingEntityListener::class) na Entidade e @EnableJpaAuditing na [SpikeDataVersionApplication.kt](src%2Fmain%2Fkotlin%2Fbr%2Fcom%2Fgabrieldragone%2Fspikedataversion%2FSpikeDataVersionApplication.kt). Sem isso, vai dar erro de DataIntegrityViolationException 
  * Os campos anotados com ela, devem ser do tipo var, se estiver com val, não irá funcionar, ocorrerá erro de campo no banco não pode ser null tbm. 
  * Ao recuperar uma entidade, alterar algum campo dela, automaticamente o updatedAt será atualizado, sem necessidade de instanciar um now().
  * Em relação ao banco, verificar como os campos devem ser criados: [V000003__alter_table_users_add_columns_date.sql](src%2Fmain%2Fresources%2Fdb%2Fmigration%2FV000003__alter_table_users_add_columns_date.sql)
* Segue o log da aplicação:
```
=====VERSION TEST BEGINS=====

newUser = User(id=null, name=Gabriel Teste 123, email=teste@email.com, version=1)
userPersisted = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=1)

userMinorVersion = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=0)
[userMinorVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userMinorVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#24]

userBiggerVersion = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=2)
[userBiggerVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userBiggerVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#24]

userEqualVersion = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=1)
newUserEqualVersion = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=1) saved

userAfterTest = User(id=24, name=Gabriel Teste 123, email=teste@email.com, version=1)
newVersion = User(id=24, name=Gabriel Teste 123, email=new-teste@email.com, version=2)

=====NEW VERSION TEST BEGINS=====
userMinorVersion = User(id=24, name=Gabriel Teste 123, email=new-teste@email.com, version=1)
[userMinorVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userMinorVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#24]

userBiggerVersion = User(id=24, name=Gabriel Teste 123, email=new-teste@email.com, version=3)
[userBiggerVersionError] Error: org.springframework.orm.ObjectOptimisticLockingFailureException
[userBiggerVersionError] Message: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [br.com.gabrieldragone.spikedataversion.entity.User#24]

userEqualVersion = User(id=24, name=Gabriel Teste 123, email=new-teste@email.com, version=2)
newUserEqualVersion = User(id=24, name=Gabriel Teste 123, email=new-teste@email.com, version=2) saved


=====VERSION TEST END=====
```