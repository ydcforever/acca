package com.btw.parser.controller;

import com.btw.parser.service.*;
import com.fate.schedule.SteerableSchedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ydc on 2020/8/29.
 */
@Api(value = "ACCA 解析")
@Controller
@RequestMapping("/acca")
public class ACCAController {

    @Autowired
    private DDpOpraService dDpOpraService;

    @Autowired
    private DIpOpraService dIpOpraService;

    @Autowired
    private DDpPraService dDpPraService;

    @Autowired
    private DIpPraService dIpPraService;

    @Autowired
    private DDpUplService dDpUplService;

    @Autowired
    private MDpUplService mDpUplService;

    @Autowired
    private DIpUplService dIpUplService;

    @Autowired
    private MIpUplService mIpUplService;

    @Autowired
    private DDpSalService dDpSalService;

    @Autowired
    private MDpSalService mDpSalService;

    @Autowired
    private DIpSalService dIpSalService;

    @Autowired
    private MIpSalService mIpSalService;

    @Autowired
    private DDpTaxService dDpTaxService;

    @Autowired
    private MDpTaxService mDpTaxService;

    @Autowired
    private DIpTaxService dIpTaxService;

    @Autowired
    private MIpTaxService mIpTaxService;

    @Autowired
    private MDpAdmService mDpAdmService;

    @Autowired
    private MIpAdmService mIpAdmService;

    @Autowired
    private MDpIwbService mDpIwbService;

    @Autowired
    private MIpIwbService mIpIwbService;

    @Autowired
    private MIpMcoService mIpMcoService;

    @Autowired
    private MDpRefService mDpRefService;

    @Autowired
    private MIpRefService mIpRefService;

    @Autowired
    private MDpRfdService mDpRfdService;

    @Autowired
    private MIpRfdService mIpRfdService;

    @Autowired
    private MDpXbgService mDpXbgService;

    @Autowired
    private MIpXbgService mIpXbgService;

    @Autowired
    private MMmAgtService mMmAgtService;

    @Autowired
    private MMmCdsService mMmCdsService;

    @Autowired
    private MMmChiService mMmChiService;

    @Autowired
    private MMmFabService mMmFabService;

    @Autowired
    private MMmFliService mMmFliService;

    @Autowired
    private MMmPahService mMmPahService;

    @Autowired
    private MMmPfaService mMmPfaService;

    @Autowired
    private MMmVcnService mMmVcnService;

    @ApiOperation(value = "D_DP_OPRA")
    @RequestMapping(value = "/d_dp_opra.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_DP_OPRA", cron = "0 30 14 ? * *")
    public void parseDDpOpra() throws Exception {
        dDpOpraService.doTask("D_DP_OPRA", "ACCA_OPRA_D");
    }

    @ApiOperation(value = "D_IP_OPRA")
    @RequestMapping(value = "/d_ip_opra.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_IP_OPRA", cron = "0 30 14 ? * *")
    public void parseDIpOpra() throws Exception {
        dIpOpraService.doTask("D_IP_OPRA", "ACCA_OPRA_D");
    }

    @ApiOperation(value = "D_DP_PRA")
    @RequestMapping(value = "/d_dp_pra.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_DP_PRA", cron = "0 30 14 ? * *")
    public void parseDDpPRA() throws Exception {
        dDpPraService.doTask("D_DP_PRA", "ACCA_PRA_D");
    }

    @ApiOperation(value = "D_IP_PRA")
    @RequestMapping(value = "/d_ip_pra.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_IP_PRA", cron = "0 30 14 ? * *")
    public void parseDIpPRA() throws Exception {
        dIpPraService.doTask("D_IP_PRA", "ACCA_PRA_D");
    }

    @ApiOperation(value = "D_DP_UPL")
    @RequestMapping(value = "/d_dp_upl.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_DP_UPL", cron = "0 30 14 ? * *")
    public void parseDDpUpl() throws Exception {
        dDpUplService.doTask("D_DP_UPL", "ACCA_UPL");
    }

    @ApiOperation(value = "M_DP_UPL")
    @RequestMapping(value = "/m_dp_upl.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_UPL", cron = "0 30 14 ? * *")
    public void parseMDpUpl() throws Exception {
        mDpUplService.doTask("M_DP_UPL", "ACCA_UPL");
    }

    @ApiOperation(value = "D_IP_UPL")
    @RequestMapping(value = "/d_ip_upl.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_IP_UPL", cron = "0 30 14 ? * *")
    public void parseDIpUpl() throws Exception {
        dIpUplService.doTask("D_IP_UPL", "ACCA_UPL");
    }

    @ApiOperation(value = "M_IP_UPL")
    @RequestMapping(value = "/m_ip_upl.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_UPL", cron = "0 30 14 ? * *")
    public void parseMIpUpl() throws Exception {
        mIpUplService.doTask("M_IP_UPL", "ACCA_UPL");
    }

    @ApiOperation(value = "D_DP_SAL")
    @RequestMapping(value = "/d_dp_sal.do", method = RequestMethod.POST)
    @ResponseBody
//    0/5 * * * * ?
    @SteerableSchedule(id = "D_DP_SAL", cron = "0 30 14 ? * *")
    public void parseDDpSal() throws Exception {
        dDpSalService.doTask("D_DP_SAL", "ACCA_SAL");
//        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @ApiOperation(value = "M_DP_SAL")
    @RequestMapping(value = "/m_dp_sal.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_SAL", cron = "0 30 14 ? * *")
    public void parseMDpSal() throws Exception {
        mDpSalService.doTask("M_DP_SAL", "ACCA_SAL");
    }

    @ApiOperation(value = "D_IP_SAL")
    @RequestMapping(value = "/d_ip_sal.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_IP_SAL", cron = "0 30 14 ? * *")
    public void parseDIpSal() throws Exception {
        dIpSalService.doTask("D_IP_SAL", "ACCA_SAL");
    }

    @ApiOperation(value = "M_IP_SAL")
    @RequestMapping(value = "/m_ip_sal.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_SAL", cron = "0 30 14 ? * *")
    public void parseMIpSal() throws Exception {
        mIpSalService.doTask("M_IP_SAL", "ACCA_SAL");
    }

    @ApiOperation(value = "D_DP_TAX")
    @RequestMapping(value = "/d_dp_tax.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_DP_TAX", cron = "0 30 14 ? * *")
    public void parseDdpTax() throws Exception {
        dDpTaxService.doTask("D_DP_TAX", "ACCA_TAX_DP");
    }

    @ApiOperation(value = "M_DP_TAX")
    @RequestMapping(value = "/m_dp_tax.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_TAX", cron = "0 30 14 ? * *")
    public void parseMDpTax() throws Exception {
        mDpTaxService.doTask("M_DP_TAX", "ACCA_TAX_DP");
    }

    @ApiOperation(value = "D_IP_TAX")
    @RequestMapping(value = "/d_ip_tax.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "D_IP_TAX", cron = "0 30 14 ? * *")
    public void parseDIpTax() throws Exception {
        dIpTaxService.doTask("D_IP_TAX", "ACCA_TAX_IP");
    }

    @ApiOperation(value = "M_IP_TAX")
    @RequestMapping(value = "/m_ip_tax.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_TAX", cron = "0 30 14 ? * *")
    public void parseMIpTax() throws Exception {
        mIpTaxService.doTask("M_IP_TAX", "ACCA_TAX_IP");
    }

    @ApiOperation(value = "M_DP_ADM")
    @RequestMapping(value = "/m_dp_adm.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_ADM", cron = "0 30 14 ? * *")
    public void parseMDpADM() throws Exception {
        mDpAdmService.doTask("M_DP_ADM", "ACCA_ADM_M");
    }

    @ApiOperation(value = "M_IP_ADM")
    @RequestMapping(value = "/m_ip_adm.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_ADM", cron = "0 30 14 ? * *")
    public void parseMIpADM() throws Exception {
        mIpAdmService.doTask("M_IP_ADM", "ACCA_ADM_M");
    }

    @ApiOperation(value = "M_DP_IWB")
    @RequestMapping(value = "/m_dp_iwb.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_IWB", cron = "0 30 14 ? * *")
    public void parseMDpIwb() throws Exception {
        mDpIwbService.doTask("M_DP_IWB", "ACCA_IWB_M");
    }

    @ApiOperation(value = "M_IP_IWB")
    @RequestMapping(value = "/m_ip_iwb.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_IWB", cron = "0 30 14 ? * *")
    public void parseMIpIwb() throws Exception {
        mIpIwbService.doTask("M_IP_IWB", "ACCA_IWB_M");
    }

    @ApiOperation(value = "M_IP_MCO")
    @RequestMapping(value = "/m_ip_mco.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_MCO", cron = "0 30 14 ? * *")
    public void parseMIpMCO() throws Exception {
        mIpMcoService.doTask("M_IP_MCO", "ACCA_MCO_IP");
    }

    @ApiOperation(value = "M_DP_REF")
    @RequestMapping(value = "/m_dp_ref.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_REF", cron = "0 30 14 ? * *")
    public void parseMDpRef() throws Exception {
        mDpRefService.doTask("M_DP_REF", "ACCA_REF_DP_M");
    }

    @ApiOperation(value = "M_IP_REF")
    @RequestMapping(value = "/m_ip_ref.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_REF", cron = "0 30 14 ? * *")
    public void parseMIpRef() throws Exception {
        mIpRefService.doTask("M_IP_REF", "ACCA_REF_IP_M");
    }

    @ApiOperation(value = "M_DP_RFD")
    @RequestMapping(value = "/m_dp_rfd.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_RFD", cron = "0 30 14 ? * *")
    public void parseMDpRfd() throws Exception {
        mDpRfdService.doTask("M_DP_RFD", "ACCA_RFD_DP_M");
    }

    @ApiOperation(value = "M_IP_RFD")
    @RequestMapping(value = "/m_ip_rfd.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_RFD", cron = "0 30 14 ? * *")
    public void parseMIpRfd() throws Exception {
        mIpRfdService.doTask("M_IP_RFD", "ACCA_RFD_IP_M");
    }

    @ApiOperation(value = "M_DP_XBG")
    @RequestMapping(value = "/m_dp_xbg.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_DP_XBG", cron = "0 30 14 ? * *")
    public void parseMDpXbg() throws Exception {
        mDpXbgService.doTask("M_DP_XBG", "ACCA_XBG_DP_M");
    }

    @ApiOperation(value = "M_IP_XBG")
    @RequestMapping(value = "/m_ip_xbg.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_IP_XBG", cron = "0 30 14 ? * *")
    public void parseMIpXbg() throws Exception {
        mIpXbgService.doTask("M_IP_XBG", "ACCA_XBG_IP_M");
    }

    @ApiOperation(value = "M_MM_AGT")
    @RequestMapping(value = "/m_mm_agt.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_AGT", cron = "0 30 14 ? * *")
    public void parseMMmAgt() throws Exception {
        mMmAgtService.doTask("M_MM_AGT", "ACCA_AGT");
    }

    @ApiOperation(value = "M_MM_CDS")
    @RequestMapping(value = "/m_mm_cds.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_CDS", cron = "0 30 14 ? * *")
    public void parseMMmCDS() throws Exception {
        mMmCdsService.doTask("M_MM_CDS", "ACCA_CDS");
    }

    @ApiOperation(value = "M_MM_CHI")
    @RequestMapping(value = "/m_mm_chi.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_CHI", cron = "0 30 14 ? * *")
    public void parseMMmChi() throws Exception {
        mMmChiService.doTask("M_MM_CHI", "ACCA_CHI");
    }

    @ApiOperation(value = "M_MM_FAB")
    @RequestMapping(value = "/m_mm_fab.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_FAB", cron = "0 30 14 ? * *")
    public void parseMMmFab() throws Exception {
        mMmFabService.doTask("M_MM_FAB", "ACCA_FAB");
    }

    @ApiOperation(value = "M_MM_FLI")
    @RequestMapping(value = "/m_mm_fli.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_FLI", cron = "0 30 14 ? * *")
    public void parseMMmFli() throws Exception {
        mMmFliService.doTask("M_MM_FLI", "ACCA_FLI");
    }

    @ApiOperation(value = "M_MM_PAH")
    @RequestMapping(value = "/m_mm_pah.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_PAH", cron = "0 30 14 ? * *")
    public void parseMMmPah() throws Exception {
        mMmPahService.doTask("M_MM_PAH", "ACCA_PAH");
    }

    @ApiOperation(value = "M_MM_PFA")
    @RequestMapping(value = "/m_mm_pfa.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_PFA", cron = "0 30 14 ? * *")
    public void parseMMmPfa() throws Exception {
        mMmPfaService.doTask("M_MM_PFA", "ACCA_PFA");
    }

    @ApiOperation(value = "M_MM_VCN")
    @RequestMapping(value = "/m_mm_vcn.do", method = RequestMethod.POST)
    @ResponseBody
    @SteerableSchedule(id = "M_MM_VCN", cron = "0 30 14 ? * *")
    public void parseMMmVcn() throws Exception {
        mMmVcnService.doTask("M_MM_VCN", "ACCA_VCN");
    }
}
