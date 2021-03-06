package com.SS.training.service.AdminSubMenus;

import com.SS.training.Input.InputValidation;
import com.SS.training.dao.BranchDAO;
import com.SS.training.dao.PublisherDAO;
import com.SS.training.entity.Author;
import com.SS.training.entity.Branch;
import com.SS.training.entity.Publisher;
import com.SS.training.service.ConnectUtil;
import com.SS.training.service.Librarian;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminBranches {
    public void adminBranches(Scanner scanner){
        boolean cont = true;
        while (cont){
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("Select what action you would like to execute:");
            System.out.println("1.)Add Library Branch\n2.)Delete Library Branch\n3.)Update Library Branch\n4.)Read All Library Branches\n5.)Exit to Admin Menu");
            String line = scanner.nextLine();
            if(InputValidation.checkInput(1,5,line)){
                int input = Integer.parseInt(line);
                if(input==1){
                    adminAddBranches(scanner);
                }else if(input==2){
                    adminDeleteBranch(scanner);
                }else if(input==3){
                    adminUpdateBranch(scanner);
                }else if(input==4){
                    readAllBranches();
                }else if(input==5){
                    System.out.println("Exiting to Main");
                    cont =false;
                }
            }
        }
    }
    protected void adminDeleteBranch(Scanner scanner) {
        try{
            ConnectUtil conUtil = ConnectUtil.getInstance();
            Connection conn = conUtil.getConnection();
            BranchDAO bdao = new BranchDAO(conn);
            List<Branch> branches = bdao.read();
            boolean cont = true;
            while(cont){
                System.out.println("Please enter the Branches you would like to DELETE.");
                System.out.print("|||||");
                Branch.readHeader();
                branches.forEach(b->{
                    System.out.print((branches.indexOf(b)+1)+".)");
                    b.read();
                });
                System.out.println((branches.size()+1)+".)exit");
                String line = scanner.nextLine();
                if(InputValidation.checkInput(1,(branches.size()+1),line)){
                    int input = Integer.parseInt(line);
                    if(input==(branches.size()+1)){
                        System.out.println("Canceling Update");
                        cont=false;
                    }
                    else{
                        Branch deleteBran = branches.get(input-1);
                        if(deleteBran.getLoans().size()==0 && deleteBran.getCopies().size()==0){
                            deleteBranch(deleteBran,conn);
                        }else{
                            deleteBran.getLoans().forEach(b-> System.out.println( b.getBorrower().getName()+ "is currently owned by" + b.getBook().getTitle() ));
                            deleteBran.getCopies().forEach(c-> System.out.println("Currently have "+ c.getNoOfCopies()+ " copies of " + c.getBook().getTitle()));
                            System.out.println("Are you sure, all these books will be deleted.Enter 1 for YES and 2 for NO");
                            boolean cont2 = true;
                            while(cont2){
                                line = scanner.nextLine();
                                if(InputValidation.checkInput(1,2,line)){
                                    input = Integer.parseInt(line);
                                    if(input==1)
                                        deleteBranch(deleteBran,conn);
                                    if(input==2){
                                        System.out.println("Operation Cancelled");
                                    }
                                    cont2=false;
                                }
                            }
                        }
                        cont=false;
                    }
                }
                else {
                    System.out.println("!!!!!Improper Input Format!!!!");
                }
            }
        }catch (SQLException e){
            System.out.println("!!!Error With Database!!!!");
        }catch (ClassNotFoundException e){
            System.out.println("!!!!Error with Database Driver!!!!");
        }
    }
    protected void deleteBranch(Branch branch, Connection conn){
        try {
            try {
                BranchDAO bdao = new BranchDAO(conn);
                bdao.delete(branch);
                conn.commit();
            } catch (SQLException e) {
                System.out.println("!!!DATABASE ERROR!!!!");
                conn.rollback();
            }
        }catch (SQLException e){}
    }
    protected void adminUpdateBranch(Scanner scanner){
        try{
            ConnectUtil conUtil = ConnectUtil.getInstance();
            Connection conn = conUtil.getConnection();
            BranchDAO bdao = new BranchDAO(conn);
            List<Branch> branches = bdao.read();
            boolean cont = true;
            while(cont){
                System.out.println("Please enter the Branch you would like to update.");
                System.out.print("|||||");
                Branch.readHeader();
                branches.forEach(b->{
                    System.out.print((branches.indexOf(b)+1)+".)");
                    b.read();
                });
                System.out.println((branches.size()+1)+".)exit");
                String line = scanner.nextLine();
                if(InputValidation.checkInput(1,(branches.size()+1),line)){
                    int input = Integer.parseInt(line);
                    if(input==(branches.size()+1)){
                        System.out.println("Canceling Update");
                        cont=false;
                    }
                    else{
                        Librarian librarian =new Librarian();
                        librarian.branchOptions(scanner,branches.get(input-1), conn);
                        cont=false;
                    }
                }
                else {
                    System.out.println("!!!!!Improper Input Format!!!!");
                }
            }
        }catch (SQLException e){
            System.out.println("!!!Error With Database!!!!");
        }catch (ClassNotFoundException e){
            System.out.println("!!!!Error with Database Driver!!!!");
        }
    }
    protected Branch adminAddBranches(Scanner scanner){
        System.out.println("Enter the name of the new Library Branch:");
        String name = scanner.nextLine().trim();
        System.out.println("Enter the address of " + name +":");
        String address = scanner.nextLine().trim();
        Branch branch = new Branch();
        branch.setBranchAddress(address);
        branch.setBranchName(name);
        addBranch(branch);
        return branch;
    }
    protected void addBranch(Branch branch){
        try {
            ConnectUtil conUtil = ConnectUtil.getInstance();
            Connection conn = conUtil.getConnection();
            try {
                BranchDAO bdao = new BranchDAO(conn);
                int id = bdao.add(branch);
                conn.commit();
                branch.setBranchId(id);
            } catch (SQLException e) {
                System.out.println("!!!Error With Database!!!!");
                conn.rollback();
            }
        }catch (ClassNotFoundException e) {
            System.out.println("!!!!Error with Database Driver!!!!");
        }catch (Exception e){
        }
    }
    protected void readAllBranches(){
        try{
            ConnectUtil conUtil = ConnectUtil.getInstance();
            Connection conn = conUtil.getConnection();
            BranchDAO bdao = new BranchDAO(conn);
            List<Branch> branches = bdao.read();
            System.out.print("|||");
            Branch.readHeader();
            branches.forEach(b->{
                System.out.print((branches.indexOf(b)+1) + ".)");
                b.read();
            });
        } catch (SQLException e){
            System.out.println("!!!Error With Database!!!!");
        }catch (ClassNotFoundException e){
            System.out.println("!!!!Error with Database Driver!!!!");
        }
    }
}
