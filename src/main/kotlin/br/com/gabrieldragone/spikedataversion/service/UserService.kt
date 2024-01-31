package br.com.gabrieldragone.spikedataversion.service

import br.com.gabrieldragone.spikedataversion.entity.User
import br.com.gabrieldragone.spikedataversion.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
): CommandLineRunner {

    override fun run(vararg args: String?) {

        println("\n".repeat(5))
        println("=====VERSION TEST BEGINS=====")
        println()

        val newUser = User(
            name = "Gabriel Teste 123",
            email = "teste@email.com"
        )
        println("[newUser] Object: $newUser")

        val userPersisted = userRepository.save(newUser)
        println("[userPersisted] Object: $userPersisted")

        println()

        // Versão Menor que atual:
        // Versão Maior que atual:
        // Versão Igual a atual:
        persistAll(userPersisted)

        val userAfterTest = userRepository.findById(userPersisted.id!!).get()
        println("[userAfterTest] Object: $userAfterTest")

        userAfterTest.email = "new-${userAfterTest.email}"
        //userAfterTest.version = 10L
        val newVersion = userRepository.save(userAfterTest)
        println("[newVersion] Object: $newVersion")
        println()

        println("=====NEW VERSION TEST BEGINS=====")

        persistAll(newVersion)

        println()
        println("=====VERSION TEST END=====")
        println("\n".repeat(5))
    }

    private fun persistAll(userPersisted: User) {
        persistMinorVersion(userPersisted)
        persistBiggerVersion(userPersisted)
        persistEqualVersion(userPersisted)
    }

    private fun persistMinorVersion(userPersisted: User) {
        try {
            val userMinorVersion = minorVersion(userPersisted)
            println("[userMinorVersion] Object: $userMinorVersion")
            userRepository.save(userMinorVersion)
            println("[userMinorVersion] Saved userMinorVersion")
        } catch (e: Exception) {
            println("[userMinorVersion] Error: ${e.javaClass.name}")
            println("[userMinorVersion] Message: ${e.message}")
        }
        println()
    }

    private fun persistBiggerVersion(userPersisted: User) {
        try {
            val userBiggerVersion = biggerVersion(userPersisted)
            println("[userBiggerVersion] Object: $userBiggerVersion")
            userRepository.save(userBiggerVersion)
            println("[userBiggerVersion] Saved userBiggerVersion")
        } catch (e: Exception) {
            println("[userBiggerVersion] Error: ${e.javaClass.name}")
            println("[userBiggerVersion] Message: ${e.message}")
        }
        println()
    }

    private fun persistEqualVersion(userPersisted: User) {
        try {
            val userEqualVersion = equalVersion(userPersisted)
            println("[userEqualVersion] Object: $userEqualVersion")
            val newUserEqualVersion = userRepository.save(userEqualVersion)
            println("[userEqualVersion] newUserEqualVersion: $newUserEqualVersion saved")
        } catch (e: Exception) {
            println("[userEqualVersion] Error: ${e.javaClass.name}")
            println("[userEqualVersion] Message: ${e.message}")
        }
        println()
    }

    private fun minorVersion(user: User): User {
        return user.copy(version = user.version.minus(1))
    }

    private fun biggerVersion(user: User): User {
        return user.copy(version = user.version.plus(1))
    }

    private fun equalVersion(user: User): User {
        return user.copy(version = user.version)
    }

}