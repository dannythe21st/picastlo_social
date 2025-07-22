package com.app.socialapp.presentation

import com.app.socialapp.data.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("")
@Tag(name = "Groups", description = "Groups API")
interface MainGroupServiceAPI {

    @PostMapping("/groups")
    @Operation(summary = "Create a group")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Group created with success"),
        ApiResponse(responseCode = "400", description = "Invalid Request - group ID is blank"),
        ApiResponse(responseCode = "403", description = "Group Id already exists"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun createGroup(@RequestBody group: CreateGroupDTO): GroupDTO

    @GetMapping("/groups/{id}")
    @Operation(summary = "Get a group by Id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Group with given id found"),
        ApiResponse(responseCode = "400", description = "Invalid Request - group ID is blank"),
        ApiResponse(responseCode = "404", description = "Group not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getGroup(@PathVariable("id") id: Long): GroupDTO

    @GetMapping("/groups")
    @Operation(summary = "Get all the groups for a user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of groups delivered"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getMyGroups(@RequestParam("page") page: Int,@RequestParam("size") size: Int): GroupsPaginationDTO


    @GetMapping("/groups/{id}/members")
    @Operation(summary = "Get group members")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Group members found"),
        ApiResponse(responseCode = "400", description = "Invalid Request - group ID is blank"),
        ApiResponse(responseCode = "404", description = "Group not found"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun getGroupMembers(@PathVariable("id") id: Long): List<String>

    @PutMapping("/groups/{id}/users/{username}")
    @Operation(summary = "Add a new member to a group")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Member successfully added to the group"),
        ApiResponse(responseCode = "400", description = "Invalid Request - group ID is invalid or username is blank"),
        ApiResponse(responseCode = "404", description = "Group not found. The user wasn't added to a new group"),
        ApiResponse(responseCode = "500", description = "Internal server error")
    ])
    fun addMember(@PathVariable("id") id: Long,
                  @PathVariable("username") username:String): GroupDTO

}