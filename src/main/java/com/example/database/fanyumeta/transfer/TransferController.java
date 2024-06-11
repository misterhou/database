package com.example.database.fanyumeta.transfer;

import com.example.database.fanyumeta.transfer.service.TransferService;
import com.example.database.fanyumeta.transfer.vo.TransferRequestBody;
import com.example.database.fanyumeta.transfer.vo.TransferResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 泰豪广哈中转服务
 */
@RestController
@RequestMapping("/service/transfer")
public class TransferController {

    @Resource
    private TransferService transferService;

    @PostMapping("/th-to-gh")
    public TransferResponseBody gh(@RequestBody TransferRequestBody transferRequestBody) {
        TransferResponseBody transferResponseBody = new TransferResponseBody();
        if (null == transferRequestBody) {
            // 请求参数不正确
            transferResponseBody.setMessage("请求参数不正确");
        } else {
            String service = transferRequestBody.getService();
            if (null == service) {
                // 服务类型不正确
                transferResponseBody.setMessage("服务类型不正确");
            } else {
                transferResponseBody = this.transferService.sendMsg2Gh(transferRequestBody);
            }
        }
        return transferResponseBody;
    }

    @PostMapping("/gh-to-th")
    public TransferResponseBody th(@RequestBody TransferRequestBody transferRequestBody) {
        TransferResponseBody transferResponseBody = new TransferResponseBody();
        if (null == transferRequestBody) {
            // 请求参数不正确
            transferResponseBody.setMessage("请求参数不正确");
        } else {
            String service = transferRequestBody.getService();
            if (null == service) {
                // 服务类型不正确
                transferResponseBody.setMessage("服务类型不正确");
            } else {
                transferResponseBody = this.transferService.sendMsg2Th(transferRequestBody);
            }
        }
        return transferResponseBody;
    }
}
