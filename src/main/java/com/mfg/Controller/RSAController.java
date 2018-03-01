package com.mfg.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mfg.Service.RSAUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;


/**
 * Created by I309908 on 5/22/2017.
 */
@Api(value = "RSA Controller", description = "Operations pertaining to RSA")
@RequestMapping("/api")
@RestController
public class RSAController {

    @ApiOperation(value = "Return the public key used to encrypt the text.")
    @GetMapping(value = "/rsa/publicKey", produces = "application/json")
    public String getPublicKey(){
            if (RSAUtils.getKeyPair() != null){
                JsonObject response = new JsonObject();
                Gson gson = new Gson();
                response.addProperty("key", new String(new BASE64Encoder().encodeBuffer(RSAUtils.getKeyPair().getPublic().getEncoded())));
                return gson.toJson(response);
            }
            return null;
    }

    @ApiOperation(value = "Return the decrypted text.")
    @PostMapping(value = "/rsa/decryption", produces = "application/json")
    public String decrypt(@RequestBody String param){
        try{
            return RSAUtils.decryptStringFromBase64(param);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Decryption failed.";
    }
}
