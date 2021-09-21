/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.camunda.zeebe.broker.bootstrap;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import io.atomix.cluster.messaging.ManagedMessagingService;
import io.camunda.zeebe.broker.PartitionListener;
import io.camunda.zeebe.broker.SpringBrokerBridge;
import io.camunda.zeebe.broker.clustering.ClusterServicesImpl;
import io.camunda.zeebe.broker.engine.impl.SubscriptionApiCommandMessageHandlerService;
import io.camunda.zeebe.broker.system.configuration.BrokerCfg;
import io.camunda.zeebe.broker.system.monitoring.BrokerHealthCheckService;
import io.camunda.zeebe.broker.system.monitoring.DiskSpaceUsageListener;
import io.camunda.zeebe.broker.transport.commandapi.CommandApiServiceImpl;
import io.camunda.zeebe.protocol.impl.encoding.BrokerInfo;
import io.camunda.zeebe.transport.impl.AtomixServerTransport;
import io.camunda.zeebe.util.sched.ActorSchedulingService;
import io.camunda.zeebe.util.sched.ConcurrencyControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BrokerStartupContextImpl implements BrokerStartupContext {

  private final BrokerInfo brokerInfo;
  private final BrokerCfg configuration;
  private final SpringBrokerBridge springBrokerBridge;
  private final ActorSchedulingService actorSchedulingService;
  private final BrokerHealthCheckService healthCheckService;

  private final List<PartitionListener> partitionListeners = new ArrayList<>();
  private final List<DiskSpaceUsageListener> diskSpaceUsageListeners = new ArrayList<>();

  private ConcurrencyControl concurrencyControl;
  private ClusterServicesImpl clusterServices;
  private AtomixServerTransport commandApiServerTransport;
  private ManagedMessagingService commandApiMessagingService;
  private CommandApiServiceImpl commandApiService;
  private SubscriptionApiCommandMessageHandlerService subscriptionApiService;

  public BrokerStartupContextImpl(
      final BrokerInfo brokerInfo,
      final BrokerCfg configuration,
      final SpringBrokerBridge springBrokerBridge,
      final ActorSchedulingService actorSchedulingService,
      final BrokerHealthCheckService healthCheckService) {

    this.brokerInfo = requireNonNull(brokerInfo);
    this.configuration = requireNonNull(configuration);
    this.springBrokerBridge = requireNonNull(springBrokerBridge);
    this.actorSchedulingService = requireNonNull(actorSchedulingService);
    this.healthCheckService = requireNonNull(healthCheckService);
  }

  @Override
  public BrokerInfo getBrokerInfo() {
    return brokerInfo;
  }

  @Override
  public BrokerCfg getBrokerConfiguration() {
    return configuration;
  }

  @Override
  public SpringBrokerBridge getSpringBrokerBridge() {
    return springBrokerBridge;
  }

  @Override
  public ActorSchedulingService getActorSchedulingService() {
    return actorSchedulingService;
  }

  @Override
  public ConcurrencyControl getConcurrencyControl() {
    return concurrencyControl;
  }

  public void setConcurrencyControl(final ConcurrencyControl concurrencyControl) {
    this.concurrencyControl = Objects.requireNonNull(concurrencyControl);
  }

  @Override
  public BrokerHealthCheckService getHealthCheckService() {
    return healthCheckService;
  }

  @Override
  public SubscriptionApiCommandMessageHandlerService getSubscriptionApiService() {
    return subscriptionApiService;
  }

  @Override
  public void setSubscriptionApiService(
      final SubscriptionApiCommandMessageHandlerService subscriptionApiService) {
    this.subscriptionApiService = subscriptionApiService;
  }

  @Override
  public void addPartitionListener(final PartitionListener listener) {
    partitionListeners.add(requireNonNull(listener));
  }

  @Override
  public void removePartitionListener(final PartitionListener listener) {
    partitionListeners.remove(requireNonNull(listener));
  }

  @Override
  public List<PartitionListener> getPartitionListeners() {
    return unmodifiableList(partitionListeners);
  }

  @Override
  public ClusterServicesImpl getClusterServices() {
    return clusterServices;
  }

  @Override
  public void setClusterServices(final ClusterServicesImpl clusterServices) {
    this.clusterServices = clusterServices;
  }

  @Override
  public void addDiskSpaceUsageListener(final DiskSpaceUsageListener listener) {
    diskSpaceUsageListeners.add(requireNonNull(listener));
  }

  @Override
  public void removeDiskSpaceUsageListener(final DiskSpaceUsageListener listener) {
    diskSpaceUsageListeners.remove((requireNonNull(listener)));
  }

  @Override
  public List<DiskSpaceUsageListener> getDiskSpaceUsageListeners() {
    return unmodifiableList(diskSpaceUsageListeners);
  }

  @Override
  public CommandApiServiceImpl getCommandApiService() {
    return commandApiService;
  }

  @Override
  public void setCommandApiService(final CommandApiServiceImpl commandApiService) {
    this.commandApiService = commandApiService;
  }

  @Override
  public AtomixServerTransport getCommandApiServerTransport() {
    return commandApiServerTransport;
  }

  @Override
  public void setCommandApiServerTransport(final AtomixServerTransport commandApiServerTransport) {
    this.commandApiServerTransport = commandApiServerTransport;
  }

  @Override
  public ManagedMessagingService getCommandApiMessagingService() {
    return commandApiMessagingService;
  }

  @Override
  public void setCommandApiMessagingService(
      final ManagedMessagingService commandApiMessagingService) {
    this.commandApiMessagingService = commandApiMessagingService;
  }
}
