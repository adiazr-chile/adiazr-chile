/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.femase.gestionweb.vo;

import java.util.ArrayList;

/**
 *
 * @author Alexander
 * @deprecated 
 */
public class GanttItemVO {
    String id;
    String name;
    ArrayList<GanttSerieValueVO> series;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<GanttSerieValueVO> getSeries() {
        return series;
    }

    public void setSeries(ArrayList<GanttSerieValueVO> series) {
        this.series = series;
    }

    
}
