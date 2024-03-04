package com.aicoa.PortForwardingTool.web.controller;

import com.aicoa.PortForwardingTool.web.entity.Client;
import com.aicoa.PortForwardingTool.web.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aicoa
 * @date 2024/3/3 16:20
 */
@RestController
@RequestMapping("/api/client")
@CrossOrigin
public class ClientController {
    @Autowired
    private ClientService clientService;
    @PostMapping("/add")
    public boolean addClient(@RequestBody Client client){
        return clientService.addClient(client);
    }
    /**
     * @Description 删除用户
     * @Param [id]
     * @return boolean
     **/
    @RequestMapping("/delete")
    public boolean delete(@RequestParam("clientId") int id){return clientService.deleteClient(id);}

    /**
     * @Description 获取所有用户
     * @Param []
     * @return boolean
     **/
    @RequestMapping("/get")
    public List<Client> getClients(){return clientService.getClients();}
}
