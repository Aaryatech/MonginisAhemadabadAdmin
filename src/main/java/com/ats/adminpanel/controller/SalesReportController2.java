package com.ats.adminpanel.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.AccessControll;
import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.AllFrIdName;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteId;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteIdResponse;
import com.ats.adminpanel.model.franchisee.SalesFrListSummery;
import com.ats.adminpanel.model.franchisee.SalesReportFranchisee;
import com.ats.adminpanel.model.salesreport.SalesReportBillwise;

@Controller
@Scope("session")
public class SalesReportController2 {

	AllFrIdNameList allFrIdNameList = new AllFrIdNameList();
	String todaysDate;

	@RequestMapping(value = "/showFranchiseeWiseBillReport", method = RequestMethod.GET)
	public ModelAndView showFranchiseeWiseBillReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showFranchiseeWiseBillReport", "showFranchiseeWiseBillReport", "1", "0",
				"0", "0", newModuleList);

		/*
		 * if (view.getError() == true) {
		 * 
		 * //model = new ModelAndView("accessDenied");
		 * 
		 * } else {
		 */
		model = new ModelAndView("reports/sales/frwiseSummeryReport");

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();
			}

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();

		}
		return model;

	}

	@RequestMapping(value = "/getFrListofAllFrForFrSummery", method = RequestMethod.GET)
	@ResponseBody
	public List<AllFrIdName> getFrListofAllFrForFrSummery(HttpServletRequest request, HttpServletResponse response) {

		return allFrIdNameList.getFrIdNamesList();
	}

	List<String> frList = new ArrayList<>();
	SalesFrListSummery salesFrListSummery = new SalesFrListSummery();

	@RequestMapping(value = "/getSaleFrwiseReport", method = RequestMethod.GET)
	public @ResponseBody SalesFrListSummery getSaleFrwiseReport(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportFranchisee> saleList = new ArrayList<>();
		List<AllFrIdName> frListFinal = new ArrayList<>();

		String fromDate = "";
		String toDate = "";
		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");

			System.out.println("selectedFrBefore------------------" + selectedFr);

			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			System.out.println("selectedFrAfter------------------" + selectedFr);

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside If all fr Selected ");

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);
			map.add("frIdList", selectedFr);

			ParameterizedTypeReference<List<SalesReportFranchisee>> typeRef = new ParameterizedTypeReference<List<SalesReportFranchisee>>() {
			};
			ResponseEntity<List<SalesReportFranchisee>> responseEntity = restTemplate.exchange(
					Constants.url + "getSaleReportFrwiseSummery", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			TreeSet<Integer> frIdListFinal = new TreeSet<Integer>();
			for (int j = 0; j < saleList.size(); j++) {
				frIdListFinal.add(saleList.get(j).getFrId());
			}
			for (int frId : frIdListFinal) {
				for (int j = 0; j < allFrIdNameList.getFrIdNamesList().size(); j++) {
					if (allFrIdNameList.getFrIdNamesList().get(j).getFrId() == frId) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}
				}
			}
			salesFrListSummery.setFrIdNamesList(frListFinal);
			salesFrListSummery.setSalesReportFranchiseeList(saleList);

			System.out.println("saleList*********************************************" + saleList.toString());

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr");
		rowData.add("Date");
		rowData.add("Type");
		rowData.add("Document");
		rowData.add("Order Ref");
		rowData.add("Dr Amt");
		rowData.add("Cr Amt");
		rowData.add("Balance");

		expoExcel.setRowData(rowData);
		int srno = 1;
		exportToExcelList.add(expoExcel);
		float drTotalAmt = 0.0f;
		float crTotalAmt = 0.0f;

		for (int j = 0; j < frListFinal.size(); j++) {

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("");
			rowData.add("" + frListFinal.get(j).getFrName());
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			for (int i = 0; i < saleList.size(); i++) {

				if (frListFinal.get(j).getFrId() == saleList.get(i).getFrId()) {

					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();

					rowData.add("" + srno);
					rowData.add(saleList.get(i).getBillDate());
					rowData.add(saleList.get(i).getType());
					rowData.add(saleList.get(i).getInvoiceNo());
					rowData.add(saleList.get(i).getOrderRef());

					if (saleList.get(i).getType().equals("INV")) {

						drTotalAmt = drTotalAmt + saleList.get(i).getGrandTotal();
						rowData.add("" + roundUp(saleList.get(i).getGrandTotal()));
					} else {
						rowData.add("0");
					}

					if (saleList.get(i).getType().equals("RET")) {
						crTotalAmt = crTotalAmt + saleList.get(i).getGrandTotal();
						rowData.add("" + roundUp(saleList.get(i).getGrandTotal()));
					} else {
						rowData.add("0");
					}

					srno = srno + 1;

					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);

				}
			}
		}
		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("Total");

		rowData.add("" + roundUp(drTotalAmt));
		rowData.add("" + roundUp(crTotalAmt));
		rowData.add("" + roundUp(drTotalAmt - crTotalAmt));

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "SaleBillWiseDate");
		session.setAttribute("reportNameNew", "Bill-wise Report");
		session.setAttribute("searchByNew", "From Date: " + fromDate + "  To Date: " + toDate + " ");
		session.setAttribute("mergeUpto1", "$A$1:$G$1");
		session.setAttribute("mergeUpto2", "$A$2:$G$2");

		return salesFrListSummery;
	}

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@RequestMapping(value = "pdf/showSummeryFrByFrPdf/{fromDate}/{toDate}/{selectedFr} ", method = RequestMethod.GET)
	public ModelAndView showSummeryFrByFrPdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/pdf/frwiseSummeryPdf");

		List<SalesReportFranchisee> saleList = new ArrayList<>();
		List<AllFrIdName> frListFinal = new ArrayList<>();

		try {
			System.out.println("Before*" + selectedFr);
			System.out.println();

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside If all fr Selected ");

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);
			map.add("frIdList", selectedFr);

			ParameterizedTypeReference<List<SalesReportFranchisee>> typeRef = new ParameterizedTypeReference<List<SalesReportFranchisee>>() {
			};
			ResponseEntity<List<SalesReportFranchisee>> responseEntity = restTemplate.exchange(
					Constants.url + "getSaleReportFrwiseSummery", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			TreeSet<Integer> frIdListFinal = new TreeSet<Integer>();
			for (int j = 0; j < saleList.size(); j++) {
				frIdListFinal.add(saleList.get(j).getFrId());
			}

			allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);
			for (int frId : frIdListFinal) {
				for (int j = 0; j < allFrIdNameList.getFrIdNamesList().size(); j++) {
					if (allFrIdNameList.getFrIdNamesList().get(j).getFrId() == frId) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}
				}
			}
			salesFrListSummery.setFrIdNamesList(frListFinal);
			salesFrListSummery.setSalesReportFranchiseeList(saleList);

			System.out.println("frListFinalfrListFinal***************" + frListFinal.toString());
			System.out.println("salesRepFrListsalesRepFrListsalesRepFrList***************" + saleList.toString());

			model.addObject("salesFrListSummery", salesFrListSummery);
			model.addObject("frList", frListFinal);
			model.addObject("salesRepFrList", saleList);
			model.addObject("FACTORYNAME", Constants.FACTORYNAME);
			model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		return model;
	}

}
