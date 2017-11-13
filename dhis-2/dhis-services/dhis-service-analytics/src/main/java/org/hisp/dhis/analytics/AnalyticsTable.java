package org.hisp.dhis.analytics;

/*
 * Copyright (c) 2004-2017, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.List;
import java.util.Date;

import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.commons.collection.UniqueArrayList;
import org.hisp.dhis.program.Program;
import org.springframework.util.Assert;

/**
 * @author Lars Helge Overland
 */
public class AnalyticsTable
{
    protected String baseName;

    protected List<AnalyticsTableColumn> dimensionColumns;

    protected Program program;
    
    protected Date created;
    
    private List<AnalyticsTablePartition> partitionTables = new UniqueArrayList<>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    protected AnalyticsTable()
    {
        this.created = new Date();
    }

    public AnalyticsTable( String baseName, List<AnalyticsTableColumn> dimensionColumns )
    {
        this.created = new Date();
        this.baseName = baseName;
        this.dimensionColumns = dimensionColumns;
    }

    public AnalyticsTable( String baseName, List<AnalyticsTableColumn> dimensionColumns, Program program )
    {
        this( baseName, dimensionColumns );
        this.program = program;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Adds an analytics partition table to this master table.
     * 
     * @param year the year.s
     * @param startDate the start date.
     * @param endDate the end date.
     * @return this analytics table.
     */
    public AnalyticsTable addPartitionTable( Integer year, Date startDate, Date endDate )
    {
        Assert.notNull( year, "Year must be specified" );
        
        AnalyticsTablePartition partitionTable = new AnalyticsTablePartition( this, year, startDate, endDate, false ); //TODO approval        
        this.partitionTables.add( partitionTable );
        return this;
    }
    
    /**
     * Adds an analytics partition table to this master table without a year.
     * 
     * @return this analytics table.
     */
    public AnalyticsTable addPartitionTable()
    {
        AnalyticsTablePartition partitionTable = new AnalyticsTablePartition( this, null, null, null, false ); //TODO approval        
        this.partitionTables.add( partitionTable );
        return this;
    }
    
    public String getTableName()
    {
        String name = baseName;

        if ( program != null )
        {
            name += PartitionUtils.SEP + program.getUid().toLowerCase();
        }

        return name;
    }

    public String getTempTableName()
    {
        String name = baseName + AnalyticsTableManager.TABLE_TEMP_SUFFIX;

        if ( program != null )
        {
            name += PartitionUtils.SEP + program.getUid().toLowerCase();
        }

        return name;
    }

    public boolean hasProgram()
    {
        return program != null;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getBaseName()
    {
        return baseName;
    }

    public List<AnalyticsTableColumn> getDimensionColumns()
    {
        return dimensionColumns;
    }

    public Program getProgram()
    {
        return program;
    }

    public Date getCreated()
    {
        return created;
    }

    public List<AnalyticsTablePartition> getPartitionTables()
    {
        return partitionTables;
    }

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    @Deprecated
    public void setDimensionColumns( List<AnalyticsTableColumn> dimensionColumns )
    {
        this.dimensionColumns = dimensionColumns;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals, toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( baseName == null ) ? 0 : baseName.hashCode() );
        result = prime * result + ( ( program == null ) ? 0 : program.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        AnalyticsTable other = (AnalyticsTable) object;
        
        if ( baseName == null )
        {
            if ( other.baseName != null )
            {
                return false;
            }
        }
        else if ( !baseName.equals( other.baseName ) )
        {
            return false;
        }
                
        if ( program == null )
        {
            if ( other.program != null )
            {
                return false;
            }
        }
        else if ( !program.equals( other.program ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return "[Table name: " + getTableName() + ", partitions: " + partitionTables + "]";
    }
}
