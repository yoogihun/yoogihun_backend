package com.ygh.infra.persistence.account;

import com.ygh.account.domain.account.entity.Account;
import com.ygh.account.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    /**
     * 단일 계좌 ID 조회
     * @param id 계좌 고유 ID
     * @return 계좌 엔티티
     */
    @Override
    public Optional<Account> findById(String id) {
        return accountJpaRepository.findById(id);
    }

    /**
     * 계좌 번호로 계좌 조회
     * @param accountNumber 계좌번호
     * @return 계좌 엔티티
     */
    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountJpaRepository.findByAccountNumber(accountNumber);
    }

    /**
     * 계좌 생성
     * @param account
     * @return
     */
    @Override
    public Account save(Account account) {
        return accountJpaRepository.save(account);
    }

    /**
     * 계좌 삭제
     * @param account
     */
    @Override
    public void delete(Account account) {
        accountJpaRepository.delete(account);
    }
}
