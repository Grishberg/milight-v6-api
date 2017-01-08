package com.jeefix.iot.milight;

import com.jeefix.iot.milight.api.FluentCommandWrapper;
import com.jeefix.iot.milight.common.HexUtils;
import com.jeefix.iot.milight.common.MilightArgumentException;
import com.jeefix.iot.milight.common.MilightCommand;
import com.jeefix.iot.milight.common.MilightException;
import com.jeefix.iot.milight.transport.MessageTransportService;

/**
 * Created by mais on 2017-01-02.
 */
public class CommandService {

    public static final int ROUTER_COMMUNICATION_PORT = 48899;
    public static final int COMMUNICATION_PORT = 5987;
    private int sequenceNumber = 0;
    protected byte sessionId1;
    protected byte sessionId2;
    protected MessageTransportService transportService;
    protected byte zoneId;
    private boolean initialized;

    public CommandService() {
    }

    public CommandService(String bridgeIp, int zone) {
        transportService = new MessageTransportService(bridgeIp);
        this.zoneId = (byte) zone;
        init();
    }

    public void init() {
        byte[] createSessionRequest = HexUtils.getStringAsHex(MilightCommand.CREATE_SESSION.getHexCommand());
        transportService.sendPackage(COMMUNICATION_PORT, createSessionRequest, (response) -> {
            sessionId1 = response[19];
            sessionId2 = response[20];
            initialized = true;
        });
    }

    public FluentCommandWrapper newMilightFlow() {
        if (isInitialized() == false) {
            throw new MilightException("Command service has not been initalized yet! Call init() method first.");
        }
        return new FluentCommandWrapper(this);
    }

    public void turnOn() {
        byte[] lightOnRequest = prepareCommand(MilightCommand.LED_ON.getHexCommand());
        transportService.sendPackage(COMMUNICATION_PORT, lightOnRequest);
    }

    public void turnOff() {
        byte[] request = prepareCommand(MilightCommand.LED_OFF.getHexCommand());
        transportService.sendPackage(COMMUNICATION_PORT, request);
    }

    public void setBrightness(int value) {
        if (value < 0 || value > 100) {
            throw new MilightArgumentException(String.format("Brightness level should be in range 0-100. Received %d", value));
        }
        int normalizedValue = (int) Math.ceil((double) value * 64 / 100);
        byte[] request = prepareCommand(String.format(MilightCommand.BRIGHTNESS_SET.getHexCommand(), normalizedValue));
        transportService.sendPackage(COMMUNICATION_PORT, request);
    }

    protected byte[] prepareCommand(String commandTypeHex) {
        // '80 00 00 00 11(length hex) (17 01)(WB1WB2) 00 SN 00 (31 00 00 08 04 01 00 00 00)(cmd) 01(zone) 00 3F(chksum) response: (88 00 00 00 03 00 SN 00)
        String commandTemplate = String.format("80 00 00 00 04 05 06 00 08 00 %s 19 00 21", commandTypeHex);
        byte[] command = HexUtils.getStringAsHex(commandTemplate);
        command[4] = (byte) (command.length - 5);
        command[5] = sessionId1;
        command[6] = sessionId2;
        command[8] = (byte) getSequenceNumber();
        command[19] = zoneId;
        command[21] = computeChecksum(command);
        return command;
    }

    protected static byte computeChecksum(byte[] messageBytes) {
        byte sum = 0;
        for (int i = 2; i <= 12; i++) {
            sum += messageBytes[messageBytes.length - i];
        }
        return sum;
    }


    protected int getSequenceNumber() {
        if (sequenceNumber >= 255) {
            sequenceNumber = 0;
        } else {
            sequenceNumber++;
        }
        return sequenceNumber;
    }

    public MessageTransportService getTransportService() {
        return transportService;
    }

    public void setTransportService(MessageTransportService transportService) {
        if (this.transportService != null) {
            throw new MilightException("Transport Service has been already set!");
        }
        this.transportService = transportService;
    }

    public byte getZoneId() {
        return zoneId;
    }

    public void setZoneId(byte zoneId) {
        this.zoneId = zoneId;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
