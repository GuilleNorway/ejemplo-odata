package com.cairone.odataexample.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sdl.odata.controller.AbstractODataController;

@RestController
@RequestMapping("/odata/appexample.svc/**")
public class ODataController extends AbstractODataController {

}
