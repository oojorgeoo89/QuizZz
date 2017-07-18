package jorge.rv.quizzz.service.usermanagement.utils;

import java.util.Date;

public interface DateHelper {
	Date getCurrentDate();

	Date getExpirationDate(Date from, int offset);
}
