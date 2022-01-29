package br.com.beautystyle;

import android.app.Application;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

import br.com.beautystyle.dao.ClienteDao;
import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.dao.ServiceDao;
import br.com.beautystyle.model.Category;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.model.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class AgendaApplication extends Application {

    WorkRequest workRequest = new PeriodicWorkRequest.Builder(AddExpenseWorker.class,1440, TimeUnit.MINUTES).build();

    @Override
    public void onCreate() {
        super.onCreate();
        criaEventoDeteste();
      //  WorkManager.getInstance(this).enqueue(workRequest);
    }

    private void criaEventoDeteste() {
        ServiceDao serviceDao = new ServiceDao();
        serviceDao.save(new Services("design de sobrancelha", new BigDecimal(20), LocalTime.of(2, 0)));
        serviceDao.save(new Services("cortar o cabelo", new BigDecimal(50), LocalTime.of(1, 30)));
        serviceDao.save(new Services("pintar as unhas", new BigDecimal(25), LocalTime.of(1, 30)));

        ClienteDao clienteDao = new ClienteDao();
        clienteDao.save(new Client("Grace Hopper.", "01-110011010"));
        clienteDao.save(new Client("Ada Lovelace", "01-010100011"));
        clienteDao.save(new Client("Carol Shaw", "01-1010101100"));
        clienteDao.save(new Client("Frances Allen", "01-0101001101"));

        EventDao eventDao = new EventDao();

        eventDao.save(new Event(LocalDate.now(), LocalTime.of(11,30),LocalTime.of(17,0),
                serviceDao.listAll(),
                clienteDao.listAll().get(2),
                Event.StatusPagamento.NAORECEBIDO,
                new BigDecimal(95)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(5,0),
                LocalTime.of(8,30),
                serviceDao.listAll().stream()
                        .filter(s->s.getName().equals("design de sobrancelha"))
                        .collect(Collectors.toList()),
                clienteDao.listAll().get(0),
                Event.StatusPagamento.RECEBIDO,
                new BigDecimal(20)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(10,0),
                LocalTime.of(11,30),
                serviceDao.listAll().stream()
                        .filter(s->s.getName().equals("cortar o cabelo"))
                        .collect(Collectors.toList()),
                clienteDao.listAll().get(1),
                Event.StatusPagamento.RECEBIDO,
                new BigDecimal(50)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(19,0),
                LocalTime.of(20,0),
                serviceDao.listAll().stream()
                        .filter(s->s.getName().equals("pintar as unhas")
                        || s.getName().equals("design de sobrancelha"))
                        .collect(Collectors.toList()),
                clienteDao.listAll().get(3),
                Event.StatusPagamento.NAORECEBIDO,
                new BigDecimal(75)));

        ExpenseDao expenseDao = new ExpenseDao();

        expenseDao.save(new Expense("amarelo, roxo, azul",new BigDecimal(55),LocalDate.now(), Category.ESMALTE, Expense.RepeatOrNot.NREPEAT));
        expenseDao.save(new Expense("",new BigDecimal(80),LocalDate.of(2022,2,5), Category.AMOLACAO, Expense.RepeatOrNot.NREPEAT));
        expenseDao.save(new Expense("Materiais de limpeza",new BigDecimal(42),LocalDate.of(2022,12,1), Category.OUTROS, Expense.RepeatOrNot.NREPEAT));
        expenseDao.save(new Expense("",new BigDecimal(300),LocalDate.of(2022,7,5), Category.LUZ, Expense.RepeatOrNot.NREPEAT));

        Expense expenses = new Expense("",new BigDecimal(300),LocalDate.of(2022,2,1), Category.ALUGUEL, Expense.RepeatOrNot.REPEAT);
        Expense expenses3 = new Expense("",new BigDecimal(200),LocalDate.of(2022,2,1), Category.LUZ, Expense.RepeatOrNot.REPEAT);


        expenseDao.save(expenses);
        expenseDao.save(expenses3);
    }
}
