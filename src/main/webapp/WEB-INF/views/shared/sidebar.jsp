<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">
	<!-- sidebar: style can be found in sidebar.less -->
	<section class="sidebar">

		<c:set var="permissions" value="${permissions}" />
		<!-- sidebar menu: : style can be found in sidebar.less -->
		<ul class="sidebar-menu">
			<li class="header">MAIN NAVIGATION</li>
			<li class="active treeview"><a href=""
				data-ng-click="appRouting('dashboard')"> <i
					class="fa fa-dashboard"></i> <span>Dashboard</span>
			</a></li>

			<!-- INVENTORY DEFINITIONS MENU -->
			<c:if test="${fn:contains(permissions, '/items') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""> <i class="fa fa-cubes"></i>
						<span>Inventory</span> <i class="fa fa-angle-left pull-right"></i>
				</a>
					<ul class="treeview-menu">
						<c:if test="${fn:contains(permissions, '/items') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('items')"><i
									class="fa fa fa-cube"></i> Items</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/storageLocations') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('storageLocations')"><i
									class="fa fa fa-cube"></i> Storage Locations</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/categories') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('categories')"><i
									class="fa fa fa-sitemap"></i> Categories</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/warehouses') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('warehouses')"><i
									class="fa fa fa-building-o"></i> Warehouses</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/units') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('units')"><i
									class="fa fa-balance-scale"></i> Unit of Measurements</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/attributes') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('attributes')"><i
									class="fa fa-columns"></i> Item Attributes</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/taxes') || fn:contains(permissions, 'all')}">
							<li><a href="" data-ng-click="appRouting('inventory/taxes')"><i
									class="fa fa-barcode"></i> Taxes</a></li>
						</c:if>

					</ul></li>
			</c:if>

			<c:if test="${fn:contains(permissions, '/vendors') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""
					data-ng-click="appRouting('vendors')"> <i class="fa fa-users"></i>
						<span>Vendors</span>
				</a></li>
			</c:if>


			<!-- PURCHASE ORDER MENU -->
			<c:if test="${fn:contains(permissions, '/purchaseOrders/view') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""> <i
						class="fa fa-shopping-bag" aria-hidden="true"></i> <span>Purchase
							Orders</span> <i class="fa fa-angle-left pull-right"></i>
				</a>
					<ul class="treeview-menu">
						<c:if test="${fn:contains(permissions, '/purchaseOrders/create') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('purchaseOrders/create')"><i
									class="fa fa-plus"></i> Create New Order</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/purchaseOrders/view') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('purchaseOrders/view')"><i
									class="fa fa-list-alt"></i> View All</a></li>
						</c:if>
					</ul></li>
			</c:if>


			<!-- KIT REQUESTS MENU -->
			<c:if test="${fn:contains(permissions, '/kits') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""
					data-ng-click="appRouting('kits')"> <i
						class="fa fa-shopping-basket"></i> <span>Kits</span>
				</a></li>
			</c:if>


		 	<!-- STOCK TRANSFERS MENU -->
			<c:if test="${fn:contains(permissions, '/inventory/transfers') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""> <i class="fa fa-ship"></i>
						<span>Stock Transfers</span> <i
						class="fa fa-angle-left pull-right"></i>
				</a>
					<ul class="treeview-menu">
						<c:if
							test="${fn:contains(permissions, '/inventory/transfers/create') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/transfers/create')"><i
									class="fa fa-plus"></i> Initiate Stock Transfer</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/inventory/transfers') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/transfers')"><i
									class="fa fa-list-alt"></i> View All</a></li>
						</c:if>

					</ul></li>
			</c:if>


			<!-- STOCK ADJUSTMENTS MENU 
			<c:if test="${fn:contains(permissions, '/inventory/adjustments') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""> <i class="fa fa-adjust"></i>
						<span>Stock Adjustments</span> <i
						class="fa fa-angle-left pull-right"></i>
				</a>
					<ul class="treeview-menu">
						<c:if
							test="${fn:contains(permissions, '/inventory/adjustments/create') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/adjustments/create')"><i
									class="fa fa-plus"></i> Initiate Stock Adjustment</a></li>
						</c:if>
						<c:if test="${fn:contains(permissions, '/inventory/adjustments') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/adjustments')"><i
									class="fa fa-list-alt"></i> View All</a></li>
						</c:if>
					</ul></li>
			</c:if>
			-->

			<c:if
				test="${fn:contains(permissions, '/inventory/reports/kits') || fn:contains(permissions, '/inventory/reports/saved') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""> <i
						class="fa fa-line-chart"></i> <span>Reports</span> <i
						class="fa fa-angle-left pull-right"></i>
				</a>
					<ul class="treeview-menu">
						<c:if
							test="${fn:contains(permissions, '/inventory/reports/kits') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/reports/kits')"><i
									class="fa fa-pie-chart"></i> Kit Reports </a></li>
						</c:if>
						<c:if
							test="${fn:contains(permissions, '/inventory/reports/items') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/reports/items')"><i
									class="fa fa-pie-chart"></i> Item Reports </a></li>
						</c:if>
						<c:if
							test="${fn:contains(permissions, '/inventory/reports/purchaseOrders') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/reports/purchaseOrders')"><i
									class="fa fa-pie-chart"></i> Purchase Order Reports </a></li>
						</c:if>
						<c:if
							test="${fn:contains(permissions, '/inventory/reports/sales') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/reports/sales')"><i
									class="fa fa-pie-chart"></i> Sales Reports </a></li>
						</c:if>
						<!-- 
						<c:if
							test="${fn:contains(permissions, '/inventory/reports/saved') || fn:contains(permissions, 'all')}">
							<li><a href=""
								data-ng-click="appRouting('inventory/reports/saved')"><i
									class="fa fa-pie-chart"></i> Inventory Count Sheets </a></li>
						</c:if> -->
					</ul></li>
			</c:if>
			<c:if test="${fn:contains(permissions, '/inventory/recommendations') || fn:contains(permissions, 'all')}">
				<li class="treeview"><a href=""
					data-ng-click="appRouting('/inventory/recommendations')"> <i
						class="fa fa-shopping-basket"></i> <span>Recommendations</span>
				</a></li>
			</c:if>
		</ul>
	</section>
	<!-- /.sidebar -->
</aside>

