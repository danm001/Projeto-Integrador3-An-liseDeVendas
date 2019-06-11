package com.github.brendonpm.dws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Component("relatorioControle")
public class RelatorioControle {
    
    @Autowired
    DataSource datasource;
    ResultSet executeQuery;
    ResultSet executeQuery2;
    ResultSet executeQuery3;
    ResultSet executeQuery4;
    
    @RequestMapping("/")
    public String index(){
        return "index.xhtml";
    }
    
    public int getQuantidadeVendas(){
        Integer tot = 0, semestre = 1;
        
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        if(month<7){
            semestre = 1;
        }
        else{
            semestre = 2;
        }
        int year = cal.get(Calendar.YEAR);
        try {
            executeQuery = datasource.getConnection().createStatement().executeQuery("SELECT COUNT(DISTINCT NUNOTA)"+
                                                                                               " FROM VENDAS VEN"+
                                                                                               " INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID"+
                                                                                               " WHERE TEM.SEMESTRE = "+Integer.toString(semestre)+" AND TEM.ANO = " + Integer.toString(year));
            executeQuery.next();
            tot = executeQuery.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(RelatorioControle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return tot;
    }
    
    public String getTotalVendas(){
        Integer tot = 0, semestre = 1;
        String fim = null;
        
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        if(month<7){
            semestre = 1;
        }
        else{
            semestre = 2;
        }
        int year = cal.get(Calendar.YEAR);
        try {
            executeQuery2 = datasource.getConnection().createStatement().executeQuery("SELECT DISTINCT NUNOTA,VALOR_VENDAS"+
                                                                                               " FROM VENDAS VEN"+
                                                                                               " INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID"+
                                                                                               " WHERE TEM.SEMESTRE = "+Integer.toString(semestre)+" AND TEM.ANO = " + Integer.toString(year));
            while(executeQuery2.next()){
                tot = tot + executeQuery2.getInt("VALOR_VENDAS");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RelatorioControle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        fim = "R$ " + Integer.toString(tot);
        
        return fim;
    }
    
    public Integer getTotalLoja(){
        Integer tot = null;
        
        try {
            executeQuery3 = datasource.getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM LOJA");
            executeQuery3.next();
            tot = executeQuery3.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(RelatorioControle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return tot;
    }
    
    public Integer getTotalClientes(){
        Integer tot = null;
        
        try {
            executeQuery4 = datasource.getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM CLIENTE");
            executeQuery4.next();
            tot = executeQuery4.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(RelatorioControle.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return tot;
    }
    
    String ano;
    String semestre;
    
    List<String[]> resultado = new ArrayList();
    
    ResultSet executeVlr;
    ResultSet executeVlr2;
    ResultSet executeVlr3;
    ResultSet executeVlr4;
    ResultSet executeVlr5;
    ResultSet executeVlr6;
    
    public void VendasLojaSemestre() throws SQLException{
        
        resultado.clear();
        executeVlr = datasource.getConnection().createStatement().executeQuery("SELECT LOJ.NOME, SUM(DISTINCT VEN.VALOR_VENDAS) AS VALOR FROM VENDAS VEN\n" +
                                                                                            " INNER JOIN LOJA LOJ ON VEN.ID_LOJA = LOJ.ID\n" +
                                                                                            " INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID\n" +
                                                                                            " WHERE TEM.ANO = "+ano+" AND TEM.SEMESTRE = "+semestre+"\n" +
                                                                                            " GROUP BY LOJ.NOME");
        while(executeVlr.next()){
            
            resultado.add(new String[]{executeVlr.getString("NOME"), "R$ "+Integer.toString(executeVlr.getInt("VALOR"))});
        }
    }
    
    public void ClienteAno() throws SQLException{
        
        resultado.clear();
        executeVlr2 = datasource.getConnection().createStatement().executeQuery("SELECT CLI.NOME, SUM(DISTINCT VEN.VALOR_VENDAS) AS VALOR FROM VENDAS VEN\n" +
                                                                                         "INNER JOIN CLIENTE CLI ON VEN.ID_CLIENTE = CLI.ID\n" +
                                                                                         "INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID\n" +
                                                                                         "WHERE TEM.ANO = "+ano+"\n" +
                                                                                         "GROUP BY CLI.NOME");
        while(executeVlr2.next()){
            
            resultado.add(new String[]{executeVlr2.getString("NOME"), "R$ "+Integer.toString(executeVlr2.getInt("VALOR"))});
        }
    }
    
    public void LojaProd() throws SQLException{
        
        resultado.clear();
        executeVlr3 = datasource.getConnection().createStatement().executeQuery("SELECT LOJ.NOME AS LOJA, PROD.DESCRICAO AS PROD, SUM(VEN.QTDE_PRODUTO) AS QTDE FROM VENDAS VEN" +
                                                                                            " INNER JOIN LOJA LOJ ON VEN.ID_LOJA = LOJ.ID" +
                                                                                            " INNER JOIN PRODUTO PROD ON VEN.ID_PRODUTO = PROD.ID" +
                                                                                            " GROUP BY LOJ.NOME, PROD.DESCRICAO" +
                                                                                            " ORDER BY LOJ.NOME");
        while(executeVlr3.next()){
            
            resultado.add(new String[]{executeVlr3.getString("LOJA"), executeVlr3.getString("PROD"), Integer.toString(executeVlr3.getInt("QTDE"))});
        }
    }
    
    String dti;
    String dtf;

    public String getDti() {
        return dti;
    }

    public void setDti(String dti) {
        this.dti = dti;
    }

    public String getDtf() {
        return dtf;
    }

    public void setDtf(String dtf) {
        this.dtf = dtf;
    }
    
    public void QtdProdVend() throws SQLException{
        
        resultado.clear();
        executeVlr4 = datasource.getConnection().createStatement().executeQuery("SELECT PROD.DESCRICAO AS PRODUTO, SUM(VEN.QTDE_PRODUTO) AS QTD FROM VENDAS VEN" +
                                                                                            " INNER JOIN PRODUTO PROD ON VEN.ID_PRODUTO = PROD.ID" +
                                                                                            " INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID" +
                                                                                            " WHERE TEM.DATA_VENDA > '"+dti+"' AND TEM.DATA_VENDA <= '"+dtf+"'" +
                                                                                            " GROUP BY PROD.DESCRICAO");
        while(executeVlr4.next()){
            
            resultado.add(new String[]{executeVlr4.getString("PRODUTO"), Integer.toString(executeVlr4.getInt("QTD"))});
        }
    }
    
    public void VendPReg() throws SQLException{
        
        resultado.clear();
        executeVlr5 = datasource.getConnection().createStatement().executeQuery("SELECT LOC.REGIAO, SUM(VEN.VALOR_VENDAS) AS VALOR FROM VENDAS VEN" +
                                                                                            " INNER JOIN LOCALIDADE LOC ON VEN.ID_LOCALIDADE = LOC.ID" +
                                                                                            " GROUP BY LOC.REGIAO");
        while(executeVlr5.next()){
            
            resultado.add(new String[]{executeVlr5.getString("REGIAO"), "R$" + Integer.toString(executeVlr5.getInt("VALOR"))});
        }
    }
    
    public void Json() throws JsonProcessingException, IOException, SQLException{
        
        List<Integer> valor = new ArrayList();
        List<String> nome = new ArrayList();
        
        FileWriter arq = new FileWriter("D:\\ResultadoOrdenado.json");
        PrintWriter gravarArq = new PrintWriter(arq);
        
        resultado.clear();
        executeVlr6 = datasource.getConnection().createStatement().executeQuery("SELECT CLI.NOME, SUM(VEN.VALOR_VENDAS) AS VALOR FROM VENDAS VEN" +
                                                                                         " INNER JOIN CLIENTE CLI ON VEN.ID_CLIENTE = CLI.ID" +
                                                                                         " INNER JOIN TEMPO TEM ON VEN.ID_TEMPO = TEM.ID" +
                                                                                         " GROUP BY CLI.NOME");
        while(executeVlr6.next()){
            valor.add(executeVlr6.getInt("VALOR"));
            nome.add(executeVlr6.getString("NOME"));
        }
        int vvalor[];
        String vnome[];
        vnome = new String[valor.size()];
        vvalor = new int[valor.size()];
        
        for(int i=0; i<valor.size(); i++){
            vvalor[i] = valor.get(i);
            vnome[i] = nome.get(i);
        }
        
//++++++++++++++++++++++++BUBBLE++++++++++++++++++++++++++++++++++++++++++++++++

                    int atual;
                    String atual2;
                    for(int i=0; i<vvalor.length; i++){
                        for(int j=0; j<vvalor.length; j++){
                            if(j+1 != vvalor.length){
                                if(vvalor[j]>vvalor[j+1]){
                                    atual = vvalor[j];
                                    atual2 = vnome[j];
                                    
                                    vvalor[j] = vvalor[j+1];
                                    vnome[j] = vnome[j+1];
                                    
                                    vvalor[j+1] = atual;
                                    vnome[j+1] = atual2;
                                }
                            }  
                        }
                    }
                           
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        
        resultado.clear();
        for(int i=0; i<vvalor.length; i++){
            resultado.add(new String[]{vnome[i], "R$ "+Integer.toString(vvalor[i])});
        }
        
        ObjectMapper converssor = new ObjectMapper();
        String str = converssor.writeValueAsString(resultado);
        
        gravarArq.print(str);
        arq.close();
        
        System.out.println(str);
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public List<String[]> getResultado() {
        return resultado;
    }

    public void setResultado(List<String[]> resultado) {
        this.resultado = resultado;
    }
    
    
}
