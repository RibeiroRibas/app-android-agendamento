package br.com.beautystyle;

import android.app.Application;

import br.com.beautystyle.dao.ClienteDao;
import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.dao.ServiceDao;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@SuppressWarnings("WeakerAccess")
public class AgendaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        criaEventoDeteste();
    }

    private void criaEventoDeteste() {
        ServiceDao serviceDao = new ServiceDao();
        serviceDao.save(new Services("sobrancelha", new BigDecimal(20), LocalTime.of(2, 0)));
        serviceDao.save(new Services("cabelo", new BigDecimal(20), LocalTime.of(1, 30)));

        ClienteDao clienteDao = new ClienteDao();
        for(int i = 0; i< 10000;i++)
            clienteDao.save(new Client(""+i, "" + (1 + 1)));
        clienteDao.save(new Client("Patricia", "48-9955246"));
        clienteDao.save(new Client("Joana", "48-84512378"));
        clienteDao.save(new Client("Maria", "48-96457894"));

        EventDao eventDao = new EventDao();
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(14,30),LocalTime.of(18,0),serviceDao.listAll(),clienteDao.listAll().get(2), Event.StatusPagamento.NAORECEBIDO,new BigDecimal(40)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(5,0),LocalTime.of(8,30),serviceDao.listAll(),clienteDao.listAll().get(0),Event.StatusPagamento.RECEBIDO,new BigDecimal(40)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(10,0),LocalTime.of(11,30),serviceDao.listAll(),clienteDao.listAll().get(1),Event.StatusPagamento.RECEBIDO,new BigDecimal(40)));
        eventDao.save(new Event(LocalDate.now(), LocalTime.of(19,0),LocalTime.of(20,0),serviceDao.listAll(),clienteDao.listAll().get(2),Event.StatusPagamento.NAORECEBIDO,new BigDecimal(40)));

    }
}
