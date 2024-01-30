package br.com.gabrieldragone.spikedataversion.repository

import br.com.gabrieldragone.spikedataversion.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
}