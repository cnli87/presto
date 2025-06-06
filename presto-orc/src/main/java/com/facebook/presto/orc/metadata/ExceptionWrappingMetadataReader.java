/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.orc.metadata;

import com.facebook.presto.orc.DwrfEncryptionProvider;
import com.facebook.presto.orc.DwrfKeyProvider;
import com.facebook.presto.orc.OrcCorruptionException;
import com.facebook.presto.orc.OrcDataSource;
import com.facebook.presto.orc.OrcDataSourceId;
import com.facebook.presto.orc.OrcDecompressor;
import com.facebook.presto.orc.metadata.PostScript.HiveWriterVersion;
import com.facebook.presto.orc.metadata.statistics.HiveBloomFilter;
import com.facebook.presto.orc.protobuf.InvalidProtocolBufferException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class ExceptionWrappingMetadataReader
        implements MetadataReader
{
    private final OrcDataSourceId orcDataSourceId;
    private final MetadataReader delegate;

    public ExceptionWrappingMetadataReader(OrcDataSourceId orcDataSourceId, MetadataReader delegate)
    {
        this.orcDataSourceId = requireNonNull(orcDataSourceId, "orcDataSourceId is null");
        this.delegate = requireNonNull(delegate, "delegate is null");
        checkArgument(!(delegate instanceof ExceptionWrappingMetadataReader), "ExceptionWrappingMetadataReader can not wrap a ExceptionWrappingMetadataReader");
    }

    @Override
    public PostScript readPostScript(byte[] data, int offset, int length)
            throws OrcCorruptionException, IOException
    {
        try {
            return delegate.readPostScript(data, offset, length);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid postscript");
        }
    }

    @Override
    public Metadata readMetadata(HiveWriterVersion hiveWriterVersion, InputStream inputStream)
            throws OrcCorruptionException, IOException
    {
        try {
            return delegate.readMetadata(hiveWriterVersion, inputStream);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid file metadata");
        }
    }

    @Override
    public Footer readFooter(HiveWriterVersion hiveWriterVersion,
            InputStream inputStream,
            DwrfEncryptionProvider dwrfEncryptionProvider,
            DwrfKeyProvider dwrfKeyProvider,
            OrcDataSource orcDataSource,
            Optional<OrcDecompressor> decompressor)
            throws OrcCorruptionException, IOException
    {
        try {
            return delegate.readFooter(hiveWriterVersion, inputStream, dwrfEncryptionProvider, dwrfKeyProvider, orcDataSource, decompressor);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid file footer");
        }
    }

    @Override
    public StripeFooter readStripeFooter(OrcDataSourceId orcDataSourceId, List<OrcType> types, InputStream inputStream)
            throws IOException
    {
        try {
            return delegate.readStripeFooter(orcDataSourceId, types, inputStream);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid stripe footer");
        }
    }

    @Override
    public List<RowGroupIndex> readRowIndexes(HiveWriterVersion hiveWriterVersion, InputStream inputStream, List<HiveBloomFilter> bloomFilters)
            throws OrcCorruptionException, IOException
    {
        try {
            return delegate.readRowIndexes(hiveWriterVersion, inputStream, bloomFilters);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid stripe row index");
        }
    }

    @Override
    public List<HiveBloomFilter> readBloomFilterIndexes(InputStream inputStream)
            throws OrcCorruptionException, IOException
    {
        try {
            return delegate.readBloomFilterIndexes(inputStream);
        }
        catch (InvalidProtocolBufferException e) {
            throw new OrcCorruptionException(e, orcDataSourceId, "Invalid bloom filter");
        }
    }
}
