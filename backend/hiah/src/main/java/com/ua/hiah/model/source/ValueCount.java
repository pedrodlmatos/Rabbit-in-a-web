package com.ua.hiah.model.source;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.annotations.Expose;
import com.ua.hiah.views.Views;

import javax.persistence.*;

@Entity
@Table(name = "VALUE_COUNT")
public class ValueCount {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.TableMapping.class)
    private Long id;

    @Column(name = "value")
    @JsonView(Views.TableMapping.class)
    @Expose
    private String value;

    @Column(name = "frequency")
    @JsonView(Views.TableMapping.class)
    @Expose
    private int frequency;

    @Column(name = "percentage")
    @JsonView(Views.TableMapping.class)
    @Expose
    private double percentage;

    @ManyToOne
    @JoinColumn(name = "source_field_id")
    @JsonIgnore
    private SourceField field;

    // CONSTRUCTORS
    public ValueCount() {
    }

    public ValueCount(String value, Integer frequency, double percentage, SourceField field) {
        this.value = value;
        this.frequency = frequency;
        this.percentage = percentage;
        this.field = field;
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public SourceField getField() {
        return field;
    }

    public void setField(SourceField field) {
        this.field = field;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "ValueCount{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
