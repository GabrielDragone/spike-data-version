package br.com.gabrieldragone.spikedataversion.repository

import br.com.gabrieldragone.spikedataversion.entity.UserWithoutVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserWithoutVersionRepository: JpaRepository<UserWithoutVersion, Long> {
}