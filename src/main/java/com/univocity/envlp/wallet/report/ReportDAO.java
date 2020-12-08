package com.univocity.envlp.wallet.report;

import com.univocity.envlp.database.*;
import com.univocity.envlp.wallet.persistence.dao.*;
import com.univocity.envlp.wallet.persistence.model.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class ReportDAO extends BaseDAO {

	public List<Object[]> getWalletAccountUsageReport(WalletSnapshot wallet) {
		String query = "" +
				" SELECT ac.account_idx AS account, ac.public_root_key AS root_key, " +
				"        0 AS total_received, count(ad.account_idx) AS addresses, 0 AS used," +
				"        ac.created_at AS created_at" +
				" FROM wallet_account ac " +
				" LEFT JOIN address_allocation ad " +
				"    ON  ac.wallet_id = ad.wallet_id " +
				"    AND ac.account_idx = ad.account_idx " +
				" WHERE " +
				"    ac.wallet_id = ? " +
				"    AND ac.account_idx <> 0 " +
				" GROUP BY ac.wallet_id, ac.account_idx " +
				" ORDER BY ac.account_idx";

		return db().query(query, ObjectArrayRowMapper.getInstance(), wallet.getId());
	}
}
