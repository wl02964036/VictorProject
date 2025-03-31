package com.rx.web.controller.important;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemMenu;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemMenuDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.web.modal.TreeNodeModel;
import com.rx.web.security.SpecialGrantedAuthority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TreeViewAjaxController {

    private static final String UNIT_ID_PREFIX = "unit_";
    private static final String USER_ID_PREFIX = "user_";

    @Autowired
    protected SystemMenuDao systemMenuDao;

    @Autowired
    protected SystemUnitDao systemUnitDao;

    @Autowired
    protected SystemUserDao systemUserDao;

    @Autowired
    protected RoleUserDao roleUserDao;

    public TreeViewAjaxController() {
        super();
    }

    private TreeNodeModel menuOf(SystemMenu systemMenu) {
        TreeNodeModel treeNode = new TreeNodeModel();
        treeNode.setId(systemMenu.getUuid());
        treeNode.setText(systemMenu.getTitle());
        treeNode.setState(null);

        if (systemMenu.getPath().equals("#")) {
            treeNode.setIcon("fas fa-folder");
            treeNode.setChildren(true);
            Map<String, String> aattrs = new HashMap<>();
            aattrs.put("class", "no_checkbox");
            treeNode.setA_attr(aattrs);
        } else {
            treeNode.setIcon("fas fa-hashtag");
            treeNode.setChildren(false);
            treeNode.setA_attr(null);
        }
        return treeNode;
    }

    private TreeNodeModel unitOf(final String prefix, final SystemUnit unit) {
        TreeNodeModel treeNode = new TreeNodeModel();
        log.info("{}", unit);
        treeNode.setId(prefix + unit.getCode());
        treeNode.setText(unit.getDisplayName());
        treeNode.setIcon("fas fa-building");
        treeNode.setData("unit");

        // 檢查是否有下一層
        Long childrenCount = systemUnitDao.countChildrenByParent(unit.getCode());
        if (childrenCount > 0L) {
            treeNode.setChildren(true);
        } else {
            Long userCount = systemUserDao.countAllByUnitCode(unit.getCode());
            if (userCount > 0L) {
                treeNode.setChildren(true);
            } else {
                treeNode.setChildren(false);
            }
        }

        return treeNode;
    }

    private TreeNodeModel userOf(final String prefix, final SystemUser user) {
        TreeNodeModel treeNode = new TreeNodeModel();
        treeNode.setId(prefix + user.getUsername());
        treeNode.setText(user.getDisplayName());
        treeNode.setIcon("fas fa-user");
        treeNode.setData("user");
        treeNode.setChildren(false);
        return treeNode;
    }

    @GetMapping(name = "tree.menus", path = "/tree/menus", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> menus(@RequestParam(name = "node") String node) {
        log.info("node is ===> {}", node);
        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {
            List<SystemMenu> topLevels = systemMenuDao.findByLevel(0);
            List<TreeNodeModel> nodes = topLevels.stream()
                    .map(path -> menuOf(path))
                    .collect(Collectors.toList());
            return nodes;
        } else {
            List<SystemMenu> children = systemMenuDao.findByParent(node);
            List<TreeNodeModel> nodes = children.stream()
                    .map(path -> menuOf(path))
                    .collect(Collectors.toList());
            return nodes;
        }
    }

    @GetMapping(name = "tree.organization", path = "/tree/organization", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> organization(@RequestParam(name = "node") String node, final ModelMap model) {
        log.info("node is ===> {}", node);
        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {

            // 如果登入者是機關管理者，以此人的單位為 root 單位
            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
            if (loginUser != null) {
                if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
                    Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(loginUser.getUnitCode());
                    if (parentUnitOpt.isEmpty()) {
                        return new ArrayList<>();
                    }

                    SystemUnit parentUnit = parentUnitOpt.get();
                    List<TreeNodeModel> unitNodes = Arrays.asList(parentUnit)
                            .stream()
                            .map(unit -> unitOf(UNIT_ID_PREFIX, unit))
                            .collect(Collectors.toList());

                    return unitNodes;
                }
            }

            List<SystemUnit> rootUnits = systemUnitDao.findAllByParent("0");
            List<TreeNodeModel> nodes = rootUnits.stream()
                    .map(unit -> unitOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());
            return nodes;
        } else {
            String unitCode = node.replace(UNIT_ID_PREFIX, "");
            Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(unitCode);
            if (parentUnitOpt.isEmpty()) {
                return new ArrayList<>();
            }

            SystemUnit parentUnit = parentUnitOpt.get();
            List<SystemUnit> childUnits = systemUnitDao.findAllByParent(parentUnit.getCode());
            List<TreeNodeModel> unitNodes = childUnits.stream()
                    .map(unit -> unitOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());

            List<SystemUser> childUsers = systemUserDao.findAllByUnitCode(parentUnit.getCode());
            List<TreeNodeModel> userNodes = childUsers.stream()
                    .map(user -> userOf(USER_ID_PREFIX, user))
                    .collect(Collectors.toList());


            return Stream.concat(unitNodes.stream(), userNodes.stream())
                    .collect(Collectors.toList());
        }
    }


    @GetMapping(name = "tree.unit", path = "/tree/unit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> unit(@RequestParam(name = "node") String node, final ModelMap model) {
        log.info("node is ===> {}", node);

        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {
            List<SystemUnit> rootUnits = systemUnitDao.findAllByParent("0");
            List<TreeNodeModel> nodes = rootUnits.stream()
                    .map(unit -> unitOf("", unit))
                    .collect(Collectors.toList());
            return nodes;
        } else {
            String unitCode = node;
            Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(unitCode);
            if (parentUnitOpt.isEmpty()) {
                return new ArrayList<>();
            }

            SystemUnit parentUnit = parentUnitOpt.get();
            List<SystemUnit> childUnits = systemUnitDao.findAllByParent(parentUnit.getCode());
            List<TreeNodeModel> unitNodes = childUnits.stream()
                    .map(unit -> unitOf("", unit))
                    .collect(Collectors.toList());

            return unitNodes;
        }

    }

    private TreeNodeModel checkedUnitOf(final String prefix, final SystemUnit unit, final List<String> selected) {
        TreeNodeModel treeNode = new TreeNodeModel();
        log.info("{}", unit);
        treeNode.setId(prefix + unit.getCode());
        treeNode.setText(unit.getDisplayName());
        treeNode.setIcon("fas fa-building");
        treeNode.setData("unit");

        if (selected.contains(unit.getCode())) {
            Map<String, Boolean> status = new HashMap<>();
            status.put("selected", true);
            treeNode.setState(status);
        }

        // 檢查是否有下一層
        Long childrenCount = systemUnitDao.countChildrenByParent(unit.getCode());
        if (childrenCount > 0L) {
            treeNode.setChildren(true);
        } else {
            Long userCount = systemUserDao.countAllByUnitCode(unit.getCode());
            if (userCount > 0L) {
                treeNode.setChildren(true);
            } else {
                treeNode.setChildren(false);
            }
        }

        return treeNode;
    }

    @GetMapping(name = "tree.checked-unit", path = "/tree/checked-unit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> checkedUnit(@RequestParam(name = "node") String node,
                                               @RequestParam(name = "selected") String selectedValues,
                                               final ModelMap model) {
        log.info("node is ===> {}", node);
        log.info("selected is ===> {}", selectedValues);

        String[] selected = selectedValues.split(",");

        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {
            List<SystemUnit> rootUnits = systemUnitDao.findAllByParent("0");
            List<TreeNodeModel> nodes = rootUnits.stream()
                    .map(unit -> checkedUnitOf("", unit, Arrays.asList(selected)))
                    .collect(Collectors.toList());
            return nodes;
        } else {
            String unitCode = node;
            Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(unitCode);
            if (parentUnitOpt.isEmpty()) {
                return new ArrayList<>();
            }

            SystemUnit parentUnit = parentUnitOpt.get();
            List<SystemUnit> childUnits = systemUnitDao.findAllByParent(parentUnit.getCode());
            List<TreeNodeModel> unitNodes = childUnits.stream()
                    .map(unit -> checkedUnitOf("", unit, Arrays.asList(selected)))
                    .collect(Collectors.toList());

            return unitNodes;
        }

    }

    private TreeNodeModel UnitDisableOf(final String prefix, final SystemUnit unit) {
        TreeNodeModel treeNode = new TreeNodeModel();
        //log.info("{}", unit);
        treeNode.setId(prefix + unit.getCode());
        treeNode.setText(unit.getDisplayName());
        treeNode.setIcon("fas fa-building");
        treeNode.setData("unit");

        // 檢查是否有下一層
        Long childrenCount = systemUnitDao.countChildrenByParent(unit.getCode());
        if (childrenCount > 0L) {
            treeNode.setChildren(true);
            Map<String, String> aattrs = new HashMap<>();
            aattrs.put("class", "no_checkbox");
            treeNode.setA_attr(aattrs);

        } else {
            Long userCount = systemUserDao.countAllByUnitCode(unit.getCode());
            if (userCount > 0L) {
                treeNode.setChildren(true);
                Map<String, String> aattrs = new HashMap<>();
                aattrs.put("class", "no_checkbox");
                treeNode.setA_attr(aattrs);

            } else {
                treeNode.setChildren(false);
                treeNode.setA_attr(null);
            }
        }

        return treeNode;
    }

    private TreeNodeModel checkedUserOf(final String prefix, final SystemUser user,
                                            final List<String> selected) {
        TreeNodeModel treeNode = new TreeNodeModel();
        treeNode.setId(prefix + user.getUsername());
        treeNode.setText(user.getDisplayName());
        treeNode.setIcon("fas fa-user");
        treeNode.setData("user");
        treeNode.setChildren(false);
        if (selected.contains(prefix + user.getUsername())) {
            Map<String, Boolean> status = new HashMap<>();
            status.put("selected", true);
            treeNode.setState(status);
        }
        return treeNode;
    }

    private TreeNodeModel checkedActUserOf(final String prefix, final SystemUser user) {
        TreeNodeModel treeNode = new TreeNodeModel();
        treeNode.setId(prefix + user.getUsername());
        treeNode.setText(user.getDisplayName());
        treeNode.setIcon("fas fa-user");
        treeNode.setData("user");
        treeNode.setChildren(false);
        Optional<RoleUser> roleUserOpt = roleUserDao.findByUserNameAndCode(user.getUsername(), "ROLE_MANAGER");
        if (roleUserOpt.isPresent()) {
            Map<String, Boolean> status = new HashMap<>();
            status.put("selected", true);
            treeNode.setState(status);
        }
        return treeNode;
    }

    @GetMapping(name = "tree.checked-user", path = "/tree/checked-user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> checkedUser(@RequestParam(name = "node") String node,
                                               @RequestParam(name = "selected") String selectedValues,
                                               final ModelMap model) {

        //log.info("node is ===> {}", node);
        //log.info("selected is ===> {}", selectedValues);

        String[] selected = selectedValues.split(",");

        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {

            // 如果登入者是機關管理者，以此人的單位為 root 單位
            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
            if (loginUser != null) {
                if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
                    Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(loginUser.getUnitCode());
                    if (parentUnitOpt.isEmpty()) {
                        return new ArrayList<>();
                    }

                    SystemUnit parentUnit = parentUnitOpt.get();
                    List<TreeNodeModel> unitNodes = Arrays.asList(parentUnit)
                            .stream()
                            .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                            .collect(Collectors.toList());

                    return unitNodes;
                }
            }

            List<SystemUnit> rootUnits = systemUnitDao.findAllByParent("0");
            List<TreeNodeModel> nodes = rootUnits.stream()
                    .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());
            return nodes;
        } else {
            String unitCode = node.replace(UNIT_ID_PREFIX, "");
            Optional<SystemUnit> parentUnitOpt = systemUnitDao.findByCode(unitCode);
            if (parentUnitOpt.isEmpty()) {
                return new ArrayList<>();
            }

            SystemUnit parentUnit = parentUnitOpt.get();
            List<SystemUnit> childUnits = systemUnitDao.findAllByParent(parentUnit.getCode());
            List<TreeNodeModel> unitNodes = childUnits.stream()
                    .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());

            List<SystemUser> childUsers = systemUserDao.findAllByUnitCode(parentUnit.getCode());
            List<TreeNodeModel> userNodes = childUsers.stream()
                    .map(user -> checkedUserOf(USER_ID_PREFIX, user, Arrays.asList(selected)))
                    .collect(Collectors.toList());


            return Stream.concat(unitNodes.stream(), userNodes.stream())
                    .collect(Collectors.toList());
        }

    }

    // 活動課程機關管理者客製(被設置機關管理者都會看到一級機關向下,除了總管者看到全部)
    @GetMapping(name = "tree.checked-user-act", path = "/tree/checked-user-act", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<TreeNodeModel> checkedUserForAct(@RequestParam(name = "node") String node,
                                                     final ModelMap model) {

        //log.info("node is ===> {}", node);

        Optional<SystemUnit> parentUnitOpt;
        if (node.equals(TreeNodeModel.ROOT_ID_SYMBOL)) {

            // 如果登入者是機關管理者，以此人的一級單位為 root 單位
            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
            if (loginUser != null) {
                if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER) ||
                        loginUser.getAuthorities().contains(SpecialGrantedAuthority.ADMIN)) {
                    if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.ADMIN)) {
                        // 總管理者看到全部
                        parentUnitOpt = systemUnitDao.findByCode("387000000A");
                    } else {
                        // 機關管理者
                        String[] pathary = loginUser.getUnit().getPath().split(",");
                        if (pathary.length < 3) {
                            return new ArrayList<>();
                        }
                        // 以自己單位所在的一級機關當root
                        parentUnitOpt = systemUnitDao.findByCode(pathary[2]);
                    }
                    if (parentUnitOpt.isEmpty()) {
                        return new ArrayList<>();
                    }
                    SystemUnit parentUnit = parentUnitOpt.get();
                    List<TreeNodeModel> unitNodes = Arrays.asList(parentUnit)
                            .stream()
                            .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                            .collect(Collectors.toList());

                    return unitNodes;
                } else {
                    return new ArrayList<>();
                }
            }
            List<SystemUnit> rootUnits = systemUnitDao.findAllByParent("0");
            List<TreeNodeModel> nodes = rootUnits.stream()
                    .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());

            return nodes;
        } else {

            String unitCode = node.replace(UNIT_ID_PREFIX, "");
            parentUnitOpt = systemUnitDao.findByCode(unitCode);
            if (parentUnitOpt.isEmpty()) {
                return new ArrayList<>();
            }

            SystemUnit parentUnit = parentUnitOpt.get();
            List<SystemUnit> childUnits = systemUnitDao.findAllByParent(parentUnit.getCode());
            List<TreeNodeModel> unitNodes = childUnits.stream()
                    .map(unit -> UnitDisableOf(UNIT_ID_PREFIX, unit))
                    .collect(Collectors.toList());

            List<SystemUser> childUsers = systemUserDao.findAllByUnitCode(parentUnit.getCode());
            List<TreeNodeModel> userNodes = childUsers.stream()
                    .map(user -> checkedActUserOf(USER_ID_PREFIX, user))
                    .collect(Collectors.toList());


            return Stream.concat(unitNodes.stream(), userNodes.stream())
                    .collect(Collectors.toList());
        }

    }

}