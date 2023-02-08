package fintx.model;

import java.time.LocalDate;
import org.immutables.value.Value;

/** A financial transaction, such as those extracted from a financial statement */
@Value.Immutable
public interface FinTransaction {

    /**
     * date of the transaction
     *
     * <p>field: 利用日, 0
     */
    LocalDate date();

    /**
     * Name describing either the store for the transaction, or the product purchased by the
     * transaction.
     *
     * <p>field: 利用店名・商品名, 1
     */
    String placeOrProduct();

    /**
     * Described which member of the account make the purchase.
     *
     * <p>field: 家族, 2
     */
    String user();

    /**
     * Describes the method of payment for the transaction
     *
     * <p>field: 支払方法, 3
     */
    String paymentMethod();

    /**
     * The cost of the product.
     *
     * <p>field: 利用金額, 4
     */
    String amountSpent();

    /**
     * The additional commmision paid.
     *
     * <p>field: 支払手数料, 5
     */
    String commisionPaid();

    /**
     * The total amount to be paid for the transaction
     *
     * <p>field: 支払総額, 6
     */
    String totalToPay();

    // not included
    // 1月支払金額
    // 2月繰越残高
    // 新規サイン
}
