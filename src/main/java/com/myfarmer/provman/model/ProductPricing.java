package com.myfarmer.provman.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "pricings")
@Data
public class ProductPricing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @NotNull
  @Column(name = "weight", nullable = false)
  private float weight;

  @NotNull
  @Column(name = "price", nullable = false)
  private float price;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;
}
